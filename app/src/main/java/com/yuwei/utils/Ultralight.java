package com.yuwei.utils;

/**
 * Created by Administrator on 2016/12/23.
 */

public class Ultralight extends CardCommon {

    public static void end() {
        ICCRF.rf_rfinf_reset(id, (byte) 0);//0区清零;
    }

    public static boolean start() {
        ICCRF.rf_rfinf_reset(id, (byte) 1);//1区写
        rf_UL_findcard();
        return true;
    }

    public static byte[] rf_UL_findcard() {
        byte[] bytes1 = new byte[8];
        byte[] len = new byte[1];
        int code = ICCRF.rf_ISO14443A_findcard(id, (byte) 0x26, len, bytes1);
        if (bytes1 != null && bytes1[0] == 0) {
            bytes1 = null;
        }
        return bytes1;
    }
    /**
     * @return 寻卡失败返回 null
     */
    public static byte[] getID() {
        ICCRF.rf_rfinf_reset(id, (byte) 1);
        byte[] bytes = rf_UL_findcard();
        log("getID", 0, bytes);
        ICCRF.rf_rfinf_reset(id, (byte) 0);
        return bytes;
    }
    /**
     * @param _Adr
     * @param _Data
     * @return 0 成功
     */
    public static int rf_UL_write(int _Adr, byte[] _Data) {
        log("rf_UL_write 页数：" + _Adr);
        start();
        int code = ICCRF.rf_UL_write(id, (byte) _Adr, _Data);
        log(_Data.length + "   rf_UL_write", code, _Data);
        end();
        return code;
    }
    public static boolean rf_UL_write(int _Adr, int _Nm, byte[] _Data) {
        log("rf_UL_writeM 页数：" + _Adr);
        start();
        boolean b = rf_UL_write_common(_Adr, _Nm, _Data);
        end();
        return b;
    }
    public static boolean rf_UL_write_common(int _Adr, int _Nm, byte[] _Data) {
        int code = -1;
        for (int i = 0; i < _Nm; i++) {
            byte[] b = new byte[4];
            System.arraycopy(_Data, 4 * i, b, 0, 4);
            code = ICCRF.rf_UL_write(id, (byte) (_Adr + i), b);
            if (code != 0) {
                log(_Data.length + "   rf_UL_writeM", code, _Data);
                return false;
            }
        }
        return true;
    }
    public static byte[] rf_UL_read(int _Adr, int _Nm) {
        log("rf_UL_read 页数：" + _Adr);
        start();
        byte[] bytes = rf_UL_read_common(_Adr, _Nm);
        end();
        return bytes;
    }

    public static byte[] rf_UL_read_common(int _Adr, int _Nm) {
        byte[] bytes = new byte[_Nm * 4];
        int code = -1;
        for (int i = 0; i < _Nm; i++) {
            byte[] b = new byte[4];
            code = ICCRF.rf_UL_read(id, (byte) (_Adr + i), b);
            if (code != 0) {
                log("   rf_UL_read", code, b);
                return null;
            } else {
                System.arraycopy(b, 0, bytes, i * 4, 4);
            }
        }
        return bytes;
    }
    public static int rf_UL_writeM(int _Adr, int _Nm, byte[] _Data) {
        log("rf_UL_writeM 页数：" + _Adr);
        start();
        int code = ICCRF.rf_UL_writeM(id, (byte) _Adr, (byte) _Nm, _Data);
        log(_Data.length + "   rf_UL_writeM", code, _Data);
        end();
        return code;
    }
    /**
     * @param _Adr
     * @param _Data
     * @return 0 成功
     */
    public static int rf_UL_read(int _Adr, byte[] _Data) {
        log("rf_UL_read 页数：" + _Adr);
        start();
        int code = ICCRF.rf_UL_read(id, (byte) _Adr, _Data);
        log(_Data.length + "   rf_UL_read", code, _Data);
        end();
        return code;
    }
    /**
     * 连续读
     *
     * @param _Adr
     * @param _Data
     * @return 0 成功
     */
    public static int rf_UL_readM(int _Adr, int _Nm, byte[] _Data) {
        log("rf_UL_readM 页数：" + _Adr);
        start();
        int code = ICCRF.rf_UL_readM(id, (byte) _Adr, (byte) _Nm, _Data);
        log(_Data.length + "   rf_UL_readM", code, _Data);
        end();
        return code;
    }
}
