package shashank.com.callerinfo.contactsActivities.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import de.hdodenhof.circleimageview.CircleImageView;
import shashank.com.callerinfo.BuildConfig;

import java.io.FileDescriptor;
import java.lang.ref.WeakReference;

/**
 * Created by sHIVAM on 9/30/2016.
 */

public abstract class ImageLoader {
    private static final String TAG = ImageLoader.class.getName();
    private static final int FADE_IN_TIME = 200;

    private ImageCache imageCache;
    private Bitmap loadingBitmap;
    private boolean fadeInBitmap = true;
    private boolean pauseWork = false;
    private final Object pauseWorkLock = new Object();
    private int imageSize;
    private Resources resources;

    protected ImageLoader(Context context, int imageSize) {
        resources = context.getResources();
        this.imageSize = imageSize;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void loadImage(Object data, CircleImageView imageView, String displayName) {

        if(data == null) {
            imageView.setImageBitmap(loadingBitmap);
            imageView.setScaleType(CircleImageView.ScaleType.CENTER_CROP);
            return;
        }

        Bitmap bitmap = null;
        if(imageCache != null) {
            bitmap = imageCache.getBitmapFromMemCache(String.valueOf(data));
        }
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(CircleImageView.ScaleType.CENTER_CROP);
        }
        else if(cancelPotentialWork(data, imageView)) {
            final BitmapWorkerTask task  = new BitmapWorkerTask(imageView);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(resources, loadingBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(data);
        }
    }

    public void setLoadingImage(int resId) {
        loadingBitmap = BitmapFactory.decodeResource(resources, resId);
    }

    public void addImageCache(FragmentManager fragmentManager, float memCacheSizePercent) {
        imageCache = ImageCache.getInstance(fragmentManager, memCacheSizePercent);
    }

    public void setImageFadeIn(boolean fadeIn) {
        fadeInBitmap = fadeIn;
    }

    protected abstract Bitmap processBitmap(Object data);

    public static void cancelWork(CircleImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
        if(bitmapWorkerTask != null) {
            bitmapWorkerTask.cancel(true);
            if (BuildConfig.DEBUG) {
                final Object bitmapData = bitmapWorkerTask.data;
                Log.d(TAG, "cancelWork - cancelled work for " + bitmapData);
            }
        }
    }

    public static boolean cancelPotentialWork(Object data, CircleImageView imageView) {
        final BitmapWorkerTask bitmapWorkertask = getBitmapWorkerTask(imageView);

        if(bitmapWorkertask != null) {
            final Object bitmapData = bitmapWorkertask.data;
            if(bitmapData == null || !bitmapData.equals(data)) {
                bitmapWorkertask.cancel(true);
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "cancelPotentialWork - cancelled work for " + data);
                }
            }else {
                return false;
            }
        }
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(CircleImageView imageView) {
        if(imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if(drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    private class BitmapWorkerTask extends AsyncTask<Object, Void, Bitmap> {

        private Object data;
        private final WeakReference<CircleImageView> imageViewWeakRef;

        public BitmapWorkerTask(CircleImageView imageView) {
            imageViewWeakRef = new WeakReference<CircleImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            if(BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackGround - starting work");
            }

            data = params[0];
            final String dataString = String.valueOf(data);
            Bitmap bitmap = null;

            synchronized (pauseWorkLock) {
                while (pauseWork && !isCancelled()) {
                    try{
                        pauseWorkLock.wait();
                    }
                    catch (InterruptedException e) {

                    }
                }
            }

            if(!isCancelled() && getAttachedCircleImageView() != null) {
                bitmap = processBitmap(params[0]);
            }

            if(bitmap != null && imageCache != null) {
                imageCache.addBitmapToCache(dataString, bitmap);
            }

            if(BuildConfig.DEBUG) {
                Log.d(TAG, "doInBackground - finished work");
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(isCancelled()) {
                bitmap = null;
            }

            final CircleImageView imageView = getAttachedCircleImageView();
            if(bitmap !=null && imageView != null) {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "onPostExecute - setting bitmap");
                }
                setImageBitmap(imageView, bitmap);
            }
        }

        @Override
        protected void onCancelled(Bitmap bitmap) {
            super.onCancelled(bitmap);
            synchronized (pauseWorkLock) {
                pauseWorkLock.notifyAll();
            }
        }

        private CircleImageView getAttachedCircleImageView() {
            final CircleImageView imageView = imageViewWeakRef.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if(this == bitmapWorkerTask) {
                return  imageView;
            }
            return null;
        }
    }

    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskWeakRef;

        public AsyncDrawable(Resources resources, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
            super(resources, bitmap);
            bitmapWorkerTaskWeakRef = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskWeakRef.get();
        }
    }

    private void setImageBitmap(CircleImageView imageView, Bitmap bitmap) {
        if(fadeInBitmap) {
            final TransitionDrawable td = new TransitionDrawable(new Drawable[] {
                    new ColorDrawable(resources.getColor(android.R.color.transparent)),
                    new BitmapDrawable(resources, bitmap)
            });
            imageView.setBackground(imageView.getDrawable());
            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }

    public void setPauseWork(boolean pauseWork) {
        synchronized (pauseWorkLock) {
            this.pauseWork = pauseWork;
            if(!pauseWork) {
                pauseWorkLock.notifyAll();
            }
        }
    }

    public static Bitmap decodeSampledBitmapFromDescriptor(FileDescriptor fileDescriptor, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if(height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/(float) reqHeight);
            final int widthRatio = Math.round((float) width/(float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            final float totalPixels = width * height;

            final float totalReqPixelsCap = reqHeight * reqWidth * 2;

            while (totalPixels/(inSampleSize*inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

}
