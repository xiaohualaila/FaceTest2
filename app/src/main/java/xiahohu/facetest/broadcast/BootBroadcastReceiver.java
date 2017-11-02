package xiahohu.facetest.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xiahohu.facetest.activity.CameraActivity;

/**
 * Created by Administrator on 2017/11/2.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)){
            Intent intent1 = new Intent();
            intent1.setClass(context, CameraActivity.class);
            intent1.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }

    }
}
