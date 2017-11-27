package xiahohu.facetest.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import xiahohu.facetest.activity.MainActivity;

/**
 * Created by Administrator on 2017/11/24.
 */

public class ScreenService extends Service {
    KeyguardManager mKeyguardManager = null;
    private KeyguardManager.KeyguardLock mKeyguardLock = null;
    BroadcastReceiver mMasterResetReciever;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        //Log.e("ScreenService","onCreate()");
        // TODO Auto-generated method stub
        startScreenService();
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Log.e("ScreenService","onStart");
        // TODO Auto-generated method stub
        startScreenService();
    }

    private void startScreenService(){
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");


        mKeyguardLock.disableKeyguard();

        //Intent.ACTION_SCREEN_OFF
        mMasterResetReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    Intent i = new Intent();
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setClass(context, MainActivity.class);
                    context.startActivity(i);
                } catch (Exception e) {
                    Log.i("mMasterResetReciever:", e.toString());
                }
            }
        };
        registerReceiver(mMasterResetReciever, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onDestroy() {
        //Log.e("ScreenService","onDestroy()");
        super.onDestroy();
        unregisterReceiver(mMasterResetReciever);
        ScreenService.this.stopSelf();
    }
}
