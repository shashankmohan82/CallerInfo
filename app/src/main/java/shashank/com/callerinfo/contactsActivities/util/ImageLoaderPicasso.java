package shashank.com.callerinfo.contactsActivities.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shashank on 10/1/2016.
 */
public class ImageLoaderPicasso {
    private static final String TAG = ImageLoaderPicasso.class.getName();
    private Resources resources;
    private Picasso picasso;
    private Bitmap loadingBitmap;

    public ImageLoaderPicasso(Context context){
        resources = context.getResources();
        int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getLargeMemoryClass();
        int cacheSize = 1024*1024*memClass/4;
        picasso = new Picasso.Builder(context).memoryCache(new LruCache(cacheSize)).build();

    }

    public void loadImage(Object data, CircleImageView imageView){
        if(data==null){
            imageView.setImageBitmap(loadingBitmap);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return;
        }

        picasso.load((Uri)data).into(imageView);


    }

    public void setLoadingBitmap(int resId){
        loadingBitmap = BitmapFactory.decodeResource(resources,resId);
    }

    public void setPauseWork(boolean flag){
        if(flag){
            picasso.pauseTag(TAG);
        }
        else{
            picasso.resumeTag(TAG);
        }
    }

}
