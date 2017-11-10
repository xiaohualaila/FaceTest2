package xiahohu.facetest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.cmm.rkadcreader.adcNative;
import com.cmm.rkgpiocontrol.rkGpioControlNative;
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
            if(val == 0){
                Log.i("sss","+++++++val++++++++++++++");
                RxBus.getDefault().post(new MyMessage(val));
                handler.postDelayed(this, TIME);

            }else {
                handler.postDelayed(this, 500);
                Log.i("sss","+++++++++++++++++++++");
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
