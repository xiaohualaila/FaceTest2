package xiahohu.facetest.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.yuwei.utils.ICCRF;
import com.yuwei.utils.Ultralight;
import xiahohu.facetest.Util.MyUtil;
import xiahohu.facetest.bean.MyMessage;
import xiahohu.facetest.rx.RxBus;

/**
 * 读卡
 */

public class MyService extends Service {

    private final int TIME = 2000;
    byte[] bytes1 = new byte[8];
    byte[] len = new byte[1];
    private int id;
    @Override
    public void onCreate() {
        super.onCreate();
        initReadCard();
    }

    private void initReadCard() {
        id = ICCRF.rf_init(0,9600);
        handler.postDelayed(runnable, TIME);
    }

    Handler handler=new Handler();

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            int result = ICCRF.rf_ISO14443A_findcard(id,(byte)0x26,len,bytes1);
         //   Log.i("ssss",result+"   >>>>>>>>>>>>>>>>>>>>>>");
            if(result != 0) {//0表示读到
                handler.postDelayed(this, 500);
            }else {
                ICCRF.rf_beep(id,10);//蜂鸣器
                String str = MyUtil.toHexString1(bytes1);
                RxBus.getDefault().post(new MyMessage(str));
                handler.postDelayed(this, TIME);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);

        ICCRF.rf_rfinf_reset(Ultralight.id, (byte) 0);
        Ultralight.offLog();
        Ultralight.exit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


}
