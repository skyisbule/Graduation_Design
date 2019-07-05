package com.github.skyisbule.db.util;

import com.github.skyisbule.db.common.DefaultConfig;

public class ByteUtil {

    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        if (bt1 == null || bt1.length == 0){
            return bt2;
        }
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static byte[] cut(byte[] data,int begin,int length){
        byte[] result = new byte[length];
        //待拷贝的数据(小的数组)   从待拷贝的哪里开始拷贝   覆盖到哪里(大的数组)  从大数组的哪里开始覆盖  覆盖多少
        System.arraycopy(data,begin,result,0,length);
        return result;
    }

    public static byte[] write(byte[] target,byte[] data,int pos){
        if (target.length == 0) return new byte[1];
        System.arraycopy(data,0,target,pos,data.length);
        return target;
    }

    public static byte[] int2byte(int sum){
        byte[] arr = new byte[4];
        arr[0] = (byte) (sum >> 24);
        arr[1] = (byte) (sum >> 16);
        arr[2] = (byte) (sum >> 8);
        arr[3] = (byte) (sum & 0xff);
        return arr;
    }

    public static int byte2int(byte[] bytes){
        return   bytes[3] & 0xFF |
                (bytes[2] & 0xFF) << 8 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[0] & 0xFF) << 24;
    }

    public static byte[] getColumnEnd(){
        byte[] bytes = new byte[1];
        bytes[0]     = DefaultConfig.COLUMN_END_FLAG;
        return bytes;
    }

    public static byte[] getDataNull(){
        byte[] bytes = new byte[1];
        bytes[0]     = DefaultConfig.DATA_NULL_FLAG;
        return bytes;
    }

}
