package xiahohu.facetest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.cmm.rkadcreader.adcNative;
import com.cmm.rkgpiocontrol.rkGpioControlNative;

import xiahohu.facetest.Util.CommonUtil;
import xiahohu.facetest.activity.CameraActivity;
import xiahohu.facetest.activity.MainActivity;
import xiahohu.facetest.bean.MyMessage;
import xiahohu.facetest.rx.RxBus;

/**
 * Created by Administrator on 2017/11/10.
 */

public class MyService extends Service {

    private final int TIME = 3000;
    @Override
    public void onCreate() {
        super.onCreate();
        initYingjian();
    }

    private void initYingjian() {
        rkGpioControlNative.init();
        handler.postDelayed(runnable, TIME);
    }

    Handler handler=new Handler();

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            int val = rkGpioControlNative.ReadGpio(4);
            Log.i("xxx",">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            if(val == 0){
                boolean flag = CommonUtil.isForeground(MyService.this,CameraActivity.class.getName());

                if(!flag){
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(getApplicationContext(),CameraActivity.class);
                    startActivity(intent);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RxBus.getDefault().post(new MyMessage(val));//发送信息
                        }
                    },2000);
                }else {
                    RxBus.getDefault().post(new MyMessage(val));
                }
                handler.postDelayed(this, TIME);
            }else {
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        adcNative.close(0);
        adcNative.close(2);
        rkGpioControlNative.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


}
