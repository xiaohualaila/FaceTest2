package xiahohu.facetest.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import xiahohu.facetest.activity.CameraActivity;

/**
 * Created by admin on 2017/11/2.
 */

public class BootBroadcastReceiver extends BroadcastReceiver{
    public static final String action_boot = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
          if(intent.getAction().equals(action_boot)){
			  Intent in  = new Intent(context,CameraActivity.class);
			  in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			  context.startActivity(in);
		  }
    }
}
