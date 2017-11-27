package com.yuwei.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


/**
 * 返回错误吗：6c80     读的时候长度超过了所创建文件的长度
 * Created by Administrator on 2016/11/25.
 */
public class CardCommon {
    public static final int SUCCESS = 0x9000;
    public static int id;
    public static final String LOG = "---Card";
    /*
     * 扫描一维码
     * @return null代表没有值，否则获取成功
     *
    public static String oneDimensionalCode() {
        int result = -1;
        byte[] bs0 = new byte[200];
        ICCRF.barcode_trig(id, (byte) 0);
        result = ICCRF.barcode_read(id, 5, bs0);
        if (result > 0) {
            String s = new String(Hex.getBytes(result, bs0));
            log("_一维码：" + s);
            return s;
        } else {
            return null;
        }
    }*/
    private static boolean isReadingCode = false;
    private static boolean isInit;

    /**
     * 蜂鸣器  大声
     *
     * @param _Msec 响应时间：单位毫秒
     */
    public static void rf_beep(final int _Msec) {
//        log("蜂鸣器："+_Msec);
        new Thread() {
            @Override
            public void run() {
                ICCRF.rf_beep(id, _Msec);
            }
        }.start();
    }

    /**
     * 扫描一维码
     *
     * @return null代表没有值，否则获取成功
     */
    public static String oneDimensionalCode() {
        if (isReadingCode) {
            return null;
        }
        isReadingCode = true;
        int result = -1;
        byte[] bs0 = new byte[200];
        ICCRF.barcode_trig(id, (byte) 0);
        result = ICCRF.barcode_read(id, 5, bs0);
        //ICCRF.rf_exit(id);
        if (result > 0) {
            byte[] bs = Hex.getBytes(result, bs0);
            String s = new String(bs);
            //System.out.println("一维码：[" + Hex.toHexString(bs) + "]");
            s = s.replaceAll("\n", "");
            String[] ss = s.split("\r");
            if (ss.length > 0) {
                s = ss[ss.length - 1];
            }
            log("一维码：[" + s + "]");
            isReadingCode = false;
            return s;
        }
        isReadingCode = false;
        return null;
    }

    /**
     * 修改波特率，默认 9600
     *
     * @param baud 读卡器波特率
     */
    public static int changbps(long baud) {
        int code = ICCRF.rf_changbps(id, baud);
        log("_修改频率,返回码：" + code);
        return code;
    }

    /**
     * 打开串口，应用启动之后调用
     *
     * @param baud 读卡器波特率
     */
    public static void init(Activity c, long baud) {
        if (!isInit) {
            id = ICCRF.rf_init(0, baud);
            isInit = true;
            log("_初始化返回id: " + id);
            if (id < 0) {
                Toast.makeText(c, "串口初始化失败，请关机后扣掉电池再重启试试", Toast.LENGTH_SHORT).show();
                c.finish();
            }
        }
    }

    public static void init(Activity c) {
        init(c, 115200); //默认给出波特率为11500；
    }

    /*
     * 关闭串口，整个应用退出之前调用
     */
    public static void exit() {
        ICCRF.rf_exit(id);
        if (mhandler != null) {
            mhandler = null;
        }
        isInit = false;
    }

    /**
     * 打印日志
     *
     * @param describe 日志内容
     */
    public static void log(String describe) {
        if (mhandler != null) {
            Message obtain = Message.obtain();
            obtain.what = 0;
            if (describe.startsWith("_")) {
                obtain.obj = describe;
            } else {
                obtain.obj = "\n------------- " + describe + " ---------------";
            }
            mhandler.sendMessage(obtain);
        }
    }

    protected static void log(String describe, int result, byte[] bs) {
        String s = describe + "返回码：" + result + "   返回值：" + Hex.toHexString(bs);
        if (mhandler != null) {
            Message obtain = Message.obtain();
            obtain.what = 0;
            obtain.obj = s;
            mhandler.sendMessage(obtain);
        }
    }

    protected static void log(String describe, int result) {
        if (mhandler != null) {
            Message obtain = Message.obtain();
            obtain.what = 0;
            obtain.obj = describe + "返回码：" + result;
            mhandler.sendMessage(obtain);
        }
    }

    /**
     * 日志 Handler
     */
    protected static Handler mhandler;

    public static void onLog(Handler handler) {
        mhandler = handler;
    }

    public static void offLog() {
        mhandler = null;
    }

    /**
     * 调用蜂鸣器
     *
     * @param ms 响应时间，单位毫秒
     */
    public static void bell(final int ms) {

    }
}
