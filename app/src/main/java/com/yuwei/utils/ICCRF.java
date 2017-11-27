package com.yuwei.utils;

public class ICCRF {

    public static int icdev = -1;


    public static byte KEY_A = 0;
    public static byte KEY_B = 4;
    public static byte _SecNo = 0, BlockNo = 0;
    public static int hCom = -1;
    public static byte[] PIN = new byte[8];


    //-----------------------------------------------------------------------------------------------
    //设备操作函数
    //-----------------------------------------------------------------------------------------------

    public static native int rf_beep(int icdev, int _Msec);

    public static native int rf_get_status(int icdev, byte[] _Status);

    public static native int rf_lib_ver(byte[] buff);

    //-----------------------------------------------------------------------------------------------
    //通用函数
    //-----------------------------------------------------------------------------------------------
    public static native int rf_init(int icdev, long baud);

    public static native int rf_exit(int icdev);

    public static native int rf_card(int icdev, byte _Mode, long[] _Snr);

    public static native int rf_config(int icdev, byte _Mode, byte _Baud);

    public static native int rf_request(int icdev, byte _Mode, byte[] TagType);

    public static native int rf_anticoll(int icdev, byte _Bcnt, long[] _Snr);

    public static native int rf_select(int icdev, long _Snr, byte[] _Size);

    /**
     * @param icdev
     * @param _Mode
     * @param _SecNr 扇区号：0到15个
     * @param _Nkey
     * @return
     */
    public static native int rf_load_key(int icdev, byte _Mode, byte _SecNr, byte[] _Nkey);

    public static native int rf_load_key_hex(int icdev, byte _Mode, byte _SecNr, byte[] _NKey);

    public static native int rf_halt(int icdev);

    /**
     * @param icdev 串口id
     * @param _Adr  每个扇区的第一块，值：1到15
     * @param _Data 读取数据，长度不得超过16个字节
     * @return true 执行成功，false失败
     */
    public static native int rf_read(int icdev, byte _Adr, byte[] _Data);

    public static native int rf_read_hex(int icdev, byte _Adr, byte[] _Data);

    /**
     * @param icdev 串口id
     * @param _Adr  每个扇区的第一块，值：1到15
     * @param _Data 写入数据，长度不得超过16个字节
     * @return true 执行成功，false失败
     */
    public static native int rf_write(int icdev, byte _Adr, byte[] _Data);

    public static native int rf_write_hex(int icdev, byte _Adr, byte[] _Data);

    public static native int rf_check_write(int icdev, long Snr, byte authmode, byte Adr, byte[] _Data);

    public static native int rf_check_writehex(int icdev, long Snr, byte authmode, byte Adr, byte[] _Data);

    //-----------------------------------------------------------------------------------------------
    //M1卡专用函数
    //-----------------------------------------------------------------------------------------------
    public static native int rf_authentication(int icdev, byte _Mode, byte _SecNr);

    public static native int rf_changeb3(int icdev, byte _SecNr, byte[] _KeyA, byte _B0, byte _B1, byte _B2, byte _B3, byte _Bk, byte[] _KeyB);

    public static native int rf_initval(int icdev, byte _Adr, long _Value);

    public static native int rf_increment(int icdev, byte _Adr, long _Value);

    public static native int rf_decrement(int icdev, byte _Adr, long _Value);

    public static native int rf_readval(int icdev, byte _Adr, long[] _Value);

    public static native int rf_restore(int icdev, byte _Adr);

    public static native int rf_transfer(int icdev, byte _Adr);

    //-----------------------------------------------------------------------------------------------
    //M1卡高级函数
    //-----------------------------------------------------------------------------------------------

    public static native int rf_HL_authentication(int icdev, byte reqmode, long snr, byte authmode, byte secnr);

    public static native int rf_HL_read(int icdev, byte _Mode, byte _Adr, long _Snr, byte[] _Data, long[] _NSnr);

    public static native int rf_HL_readhex(int icdev, byte _Mode, byte _Adr, long _Snr, byte[] _Data, long[] _NSnr);

    public static native int rf_HL_write(int icdev, byte _Mode, byte _Adr, long _Snr, byte[] _Data);

    public static native int rf_HL_writehex(int icdev, byte _Mode, byte _Adr, long _Snr, byte[] _Data);

    public static native int rf_HL_initval(int icdev, byte _Mode, byte _SecNr, long _Value, long[] _Snr);

    public static native int rf_HL_increment(int icdev, byte _Mode, byte _SecNr, long _Value, long _Snr, long[] _NValue, long[] _NSnr);

    public static native int rf_HL_decrement(int icdev, byte _Mode, byte _SecNr, long _Value, long _Snr, long[] _NValue, long[] _NSnr);

    public static native int rf_readM(int icdev, byte _Adr, byte _Nm, byte[] _Data);

    public static native int rf_writeM(int icdev, byte _Adr, byte _Nm, byte[] _Data);

    //-----------------------------------------------------------------------------------------------
    //ML卡专用函数
    //-----------------------------------------------------------------------------------------------
    public static native int rf_authentication_2(int icdev, byte _Mode, byte KeyNr, byte Adr);

    public static native int rf_initval_ml(int icdev, int _Value);

    public static native int rf_readval_ml(int icdev, int[] _Value);

    public static native int rf_decrement_transfer(int icdev, byte Adr, long _Value);

    public static native int rf_change_ml(int icdev, byte _Mode, long _Snr, byte[] _KeyA, byte _AC0, byte _AC1, byte[] _KeyB, long[] _NSnr);

    //-----------------------------------------------------------------------------------------------
    //14443 标准接口
    //-----------------------------------------------------------------------------------------------
    public static native int rf_ISO14443A_request(int icdev, byte rmode, byte[] atq);

    public static native int rf_ISO14443A_anticoll(int icdev, byte cmd, byte[] snr);

    public static native int rf_ISO14443A_select(int icdev, byte cmd, byte[] snr);

    public static native int rf_ISO14443A_findcard(int icdev, byte wmode, byte[] len, byte[] serial_nm);

    public static native int rf_ISO14443A_ATS(int icdev, byte[] len, byte[] rats);

    public static native int rf_ISO14443A_APDU(int icdev, byte slen, byte[] sbuf, byte[] rlen, byte[] rbuf);

    public static native int rf_ISO14443A_Deselect(int icdev);

    //-----------------------------------------------------------------------------------------------
    //Ultralight CARD
    //-----------------------------------------------------------------------------------------------
    public static native int rf_UL_findcard(int icdev, byte wmode, byte[] len, byte[] serial_nm);

    public static native int rf_UL_read(int icdev, byte _Adr, byte[] _Data);

    public static native int rf_UL_write(int icdev, byte _Adr, byte[] _Data);

    /**
     * Ultralight 连续读
     *
     * @param icdev
     * @param _Adr
     * @param _Nm   共读几页
     * @param _Data
     * @return
     */
    public static native int rf_UL_readM(int icdev, byte _Adr, byte _Nm, byte[] _Data);

    /**
     * Ultralight 连续写
     *
     * @param icdev
     * @param _Adr
     * @param _Nm   共写几页
     * @param _Data
     * @return
     */
    public static native int rf_UL_writeM(int icdev, byte _Adr, byte _Nm, byte[] _Data);

    public static native int rf_rfinf_reset(int icdev, byte rflag);

    public static native int des_code(byte k, byte[] pdata, byte[] dkey, byte[] res);

    public static native int rf_outputIO(int icdev, byte o_NO, byte o_lev);

    public static native int rf_changbps(int icdev, long baud);

    public static native int rf_inputIO(int icdev, byte[] o_lev);

    public static native int rf_autoread(int icdev, long flg);

    //----------------------------------------------------------------------------------------------
    //125K CARD
    //----------------------------------------------------------------------------------------------
    public static native int rf_init_125KCard(int icdev, long baud);

    public static native int rf_read_125Card_hex(int icdev, byte[] _Data);

    public static native boolean rf_PowerRFIDOff();

    public static native boolean rf_PowerRFIDON();

    //----------------------------------------------------------------------------------------------
    //cpu卡
    //----------------------------------------------------------------------------------------------
    public static native long cpu_test(int icdev);

    public static native long cpu_reset(int icdev, long[] rlen, byte[] buf);

    public static native long cpu_power_off(int icdev);

    public static native long cpu_access(int icdev, byte slen, byte[] send, byte[] rlen, byte[] receive);

    public static native long cpu_selsocket(int icdev, byte samso);

    public static native long cpu_getsocket(int icdev, byte[] samso);

    public static native long cpu_reset2(int icdev, byte baud, byte ucVoltage, long[] rlen, byte[] buf);

    //-----------------------------------------------------------------------------------------------
    //ISO15693 CARD
    //-----------------------------------------------------------------------------------------------
    public static native int ISO15693_Inventory(int icdev, byte flags, byte AFI, byte masklengh, byte[] maskvalue, byte[] databuffer);

    public static native int ISO15693_Stay_Quiet(int icdev, byte flags, byte[] UID);

    public static native int ISO15693_Read(int icdev, byte flags, byte Firstblock, byte Number, byte[] UID, byte[] databuffer);

    public static native int ISO15693_Write(int icdev, byte flags, byte Firstblock, byte Number, byte[] UID, byte[] databuffer);

    public static native int ISO15693_Lock_Block(int icdev, byte flags, byte block, byte[] UID);

    public static native int ISO15693_Select(int icdev, byte flags, byte[] UID);

    public static native int ISO15693_Reset_To_Ready(int icdev, byte flags, byte[] UID);

    public static native int ISO15693_Write_AFI(int icdev, byte flags, byte AFI, byte[] UID);

    public static native int ISO15693_Lock_AFI(int icdev, byte flags, byte[] UID);

    public static native int ISO15693_Write_DSFID(int icdev, byte flags, byte DSFID, byte[] UID);

    public static native int ISO15693_Lock_DSFID(int icdev, byte flags, byte[] UID);

    public static native int ISO15693_GET_System_Information(int icdev, byte flags, byte[] UID, byte[] databuffer);

    public static native int ISO15693_GetMultipleBlockSecurity(int icdev, byte flags, byte Firstblock, byte Number, byte[] UID, byte[] databuffer);

    public static native int ISO15693_Generic(int icdev, byte length, byte[] databuffer);

    public static native int barcode_trig(int icdev, byte ms);

    public static native int barcode_read(int icdev, int ms, byte[] samso);

    static {
//		System.loadLibrary("serialApi");
        System.loadLibrary("iccrf");
//		System.loadLibrary("com_yuwei_utils_ModuleControl");
//		System.out.println("加载库文件成功");

    }
}