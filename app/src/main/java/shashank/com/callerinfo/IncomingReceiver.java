package shashank.com.callerinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.zip.Inflater;

/**
 * Created by shashank on 9/25/2016.
 */
public class IncomingReceiver extends BroadcastReceiver {


    private WindowManager.LayoutParams params1;
    private WindowManager wm;
    private Inflater inflater;
    private LinearLayout ly1;
    private TextView tv1;
    private TextView tv2;
    private final static String TAG = IncomingReceiver.class.getName();

    @Override
    public void onReceive(final Context context, final Intent intent) {





        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        // Adds a view on top of the dialer app when it launches.
        if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)
                ||state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Log.d(TAG,"Phone ringing");
            final Intent i = new Intent(context,OverlayActivity.class);
            i.putExtras(intent);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            try {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(i);
                    }
                }, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }




        }
        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)
                || state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            try {
                final Intent intent1 = new Intent(context,OverlayActivity.class);
                intent1.putExtras(intent);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.startActivity(intent1);
                        }
                    }, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (state.equals(TelephonyManager.CALL_STATE_IDLE)
                ){
            try {
                final Intent intent1 = new Intent(context,OverlayActivity.class);
                intent1.putExtras(intent);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.startActivity(intent1);
                        }
                    }, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)
                ){
            try {
                final Intent intent1 = new Intent(context,OverlayActivity.class);
                intent1.putExtras(intent);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.startActivity(intent1);
                        }
                    }, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
