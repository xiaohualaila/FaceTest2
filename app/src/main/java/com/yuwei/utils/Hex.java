package com.yuwei.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/4.
 */
public class Hex {
    private final static String mm = "0123456789ABCDEF";

    public static byte[] getBytes(byte[] len, byte[] bs) {
        return getBytes(len[0], bs);
    }

    public static byte[] getBytes(int len, byte[] bs) {
        try {
            byte[] bytes = new byte[len];
            System.arraycopy(bs, 0, bytes, 0, len);
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] listTobytes(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    public static List<Byte> bytesToList(byte[] list) {
        List<Byte> l = new ArrayList<Byte>();
        for (int i = 0; i < list.length; i++) {
            l.add(list[i]);
        }
        return l;
    }


    public static byte[] bytePlusbByte(byte[] b1, byte[] b2) {
        byte[] bytes = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, bytes, 0, b1.length);
//        System.out.println("------------ "+Hex.toHexString(bytes));
        System.arraycopy(b2, 0, bytes, b1.length, b2.length);
//        System.out.println("------------ "+Hex.toHexString(bytes));
        return bytes;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param bytes 要转换的字节数组
     * @return 转换后的结果
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1) {
//            System.out.println("this byteArray must not be null or empty");
            return null;
        }


        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(List<Byte> byteArray) {
        if (byteArray == null || byteArray.size() < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.size(); i++) {
            if ((byteArray.get(i) & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray.get(i)));
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * 将List<Byte> 转换字符
     *
     * @param bs List<Byte> 字节数组
     * @return int int 整数
     */
    public static String byteListToString(List<Byte> bs) {
        if (bs == null || bs.size() == 0) {
            return "";
        }
        byte[] re = listToByteArray(bs);
        return byteArrayToString(re);
    }

    /**
     * 将byte[] 转换字符
     *
     * @param bs byte[] 字节数组
     * @return int int 整数
     */
    public static String byteArrayToString(byte[] bs) {
        if (bs == null || bs.length == 0) {
            return "";
        }
        int count = 0;
        for (int i = 0; i < bs.length; i++) {
            if (bs[i] == (byte) 0x00) {
                break;
            }
            count++;
        }
        if (count == 0) {
            return "";
        }

        byte[] re = new byte[count];
        System.arraycopy(bs, 0, re, 0, count);
        return new String(re);
    }

    /**
     * 将两 byte[] 数组 合并 为 byte[]
     *
     * @param bss 16进制字节数组
     * @return byte[] 字节数组s
     */
    public static byte[] mergeBytes(byte[]... bss) {
        int length = 0;
        for (int i = 0; i < bss.length; i++) {
            length += bss[i].length;
        }
        if (length == 0) {
            return null;
        }

        byte[] bytes = new byte[length];
        int index = 0;
        for (int i = 0; i < bss.length; i++) {
            System.arraycopy(bss[i], 0, bytes, index, bss[i].length);
            index += bss[i].length;
        }
        return bytes;
    }

    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param hex 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] toByteArray(String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }

        hex = hex.toUpperCase();
        byte[] bs = new byte[hex.length() / 2];

        for (int i = 0; i < hex.length(); i++) {
            char h = hex.charAt(i);
            char l = hex.charAt(++i);

            int xh = mm.indexOf(h);
            int xl = mm.indexOf(l);
            if (xh == -1) {
                throw new IllegalArgumentException("含非法16进制字符");
            }

            if (xl == -1) {
                throw new IllegalArgumentException("含非法16进制字符");
            }
            int x = xl + (xh << 4);
            bs[i / 2] = (byte) x;
        }
        return bs;
    }

    /**
     * 将List<Byte> 转为 byte[] 数组
     *
     * @param list 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] listToByteArray(List<Byte> list) {
        if (list == null) {
            return null;
        }
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }


    /**
     * 将List<Byte> 转为 byte[] 数组
     *
     * @param bytes 16进制表示的字符串
     * @return 字节数组
     */
    public static List<Byte> byteArrayToList(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        List<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < bytes.length; i++) {
            list.add(bytes[i]);
        }
        return list;
    }


    /***************************************************************************************/
    //long类型转成byte数组
    public static byte[] longToByte(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    //byte数组转成long
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
        long s4 = b[4] & 0xff;// 最低位
        long s5 = b[5] & 0xff;
        long s6 = b[6] & 0xff;
        long s7 = b[7] & 0xff;

        // s0不变
        s1 <<= 8;
        s2 <<= 16;
        s3 <<= 24;
        s4 <<= 8 * 4;
        s5 <<= 8 * 5;
        s6 <<= 8 * 6;
        s7 <<= 8 * 7;
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
        return s;
    }


    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }


    public static int byteToInt(byte[] b) {
        int s = 0;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }

    /**
     * byte 高四位 ，底四位拼接
     *
     * @param b1 高四位
     * @param b2 底四位
     * @return
     */

    public static byte b4(byte b1, byte b2) {
        byte bb = (byte) (b1 << 4);
        bb += b2;
        return bb;
    }

    public static byte[] b4(byte b) {
        byte[] bs = new byte[2];
        bs[0] = (byte) ((b >> 4) & 0xf);
        bs[1] = (byte) (b & 0xf);
        return bs;
    }

    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();//
            System.out.println(Integer.toHexString(b[i]));
//            将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    public static byte[] shortToByte(String number) {
        return shortToByte(Short.parseShort(number));
    }

    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }
//    Android开发中NFC最常用的四种读写模式：Ndef、NdefFormatable、MifareClassic、IsoDep。UltraLight存储格式共512位，分为16块，每块为4个字节，以四个字节为单位进行操作。  第0、1块存放着卡的序列号等信息，只可读。 第2块为LOCK BYTES, 设置字节2和字节3对应的位可以将从第3块到15块单独地锁定为只读区域。第3页为OTP，即一次性编程，初始值为0。写入的值和当前值进行位或操作得到新的值。这个过程是不可逆转的。如果一个位被置为1，将再也不能置回0。第4到15块为数据块。MifareClassic拥有UltraLight的全部功能，增加了两套密码，安全性更高。以16个字节为单位进行操作。IsoDep是以命令的方式
}
