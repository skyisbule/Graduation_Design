package com.github.skyisbule.db.util;

public class ByteUtil {

    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static byte[] cut(byte[] data,int begin,int length){
        byte[] result = new byte[length];
        //待拷贝的数据   从哪里开始拷贝   拷贝到哪里  从目标的哪里开始拷贝  拷贝多少
        System.arraycopy(data,begin,result,0,length);
        return result;
    }

}
