package shashank.com.callerinfo.contactsActivities.util;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.LruCache;

import shashank.com.callerinfo.BuildConfig;

/**
 * Created by sHIVAM on 9/30/2016.
 */

public class ImageCache {
    private static final String TAG = ImageCache.class.getName();
    private LruCache<String, Bitmap> memoryCache;

    private ImageCache(float memCacheSizePercent) {
        init(memCacheSizePercent);
    }

    public static ImageCache getInstance(FragmentManager fragmentManager, float memCacheSizePercentage) {
        final RetainFragment retainFragment = findOrCreateRetainFragment(fragmentManager);
        ImageCache imageCache = (ImageCache) retainFragment.getObject();

        if(imageCache == null) {
            imageCache = new ImageCache(memCacheSizePercentage);
            retainFragment.setObject(imageCache);
        }
        return imageCache;
    }

    private void init(float memCacheSizePercent) {
        int memCacheSize = calculateMemCacheSize(memCacheSizePercent);

        if(BuildConfig.DEBUG) {
            Log.d(TAG, "Memory cache create (size = "+memCacheSize);
        }

        memoryCache = new LruCache<String, Bitmap>(memCacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                final int bitmapSize = getBitmapSize(value)/1024;
                return bitmapSize == 0 ? 1 : bitmapSize;
            }
        };
    }

    public void addBitmapToCache(String data, Bitmap bitmap) {
        if(data == null || bitmap == null) {
            return;
        }
        if(memoryCache != null && memoryCache.get(data) == null) {
            memoryCache.put(data, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String data) {
        if(memoryCache != null) {
            final Bitmap bitmap = memoryCache.get(data);
            if(bitmap != null) {
                if(BuildConfig.DEBUG) {
                    Log.d(TAG, "Memory cache hit");
                }
                return bitmap;
            }
        }
        return null;
    }

    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getByteCount();
    }

    public static int calculateMemCacheSize(float percent) {
        if(percent < 0.05f || percent > 0.8f) {
            throw new IllegalArgumentException("setMemCacheSizePercent - "+ percent+" between 0.05 and 0.8");
        }
        return Math.round(percent*Runtime.getRuntime().maxMemory() / 1024);
    }

    public static RetainFragment findOrCreateRetainFragment(FragmentManager fragmentManager) {
        RetainFragment retainFragment = (RetainFragment) fragmentManager.findFragmentByTag(TAG);

        if(retainFragment == null) {
            retainFragment = new RetainFragment();
            fragmentManager.beginTransaction().add(retainFragment, TAG).commitAllowingStateLoss();
        }
        return retainFragment;
    }

    public static class RetainFragment extends Fragment {
        private Object object;

        public RetainFragment(){}

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void setObject(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

}
