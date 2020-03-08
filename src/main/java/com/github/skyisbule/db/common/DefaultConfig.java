package com.github.skyisbule.db.common;

public class DefaultConfig {

    public static byte COLUMN_END_FLAG  = 3;
    public static byte ALIVE_FLAG       = 6;
    public static byte DATA_NULL_FLAG   = 0;

    //从文件中读取数据，默认攒到五万就刷盘
    public static Integer FLUSH_DISK_FLAG_NUM = 50000;

    public static String BASE_WORK_PATH   = "/Users/mac/Desktop/";

    public static Integer COLUMN_MAX_LENGTH = 50;

}
