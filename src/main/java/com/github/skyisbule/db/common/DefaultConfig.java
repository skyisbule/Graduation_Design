package com.github.skyisbule.db.common;

public interface DefaultConfig {

    byte COLUMN_END_FLAG  = 3;
    byte ALIVE_FLAG       = 6;
    byte DATA_NULL_FLAG   = 0;

    //从文件中读取数据，默认攒到五万就刷盘
    Integer FLUSH_DISK_FLAG_NUM = 50000;

    String BASE_WORK_PATH   = "/Users/mac/Desktop/";

    Integer COLUMN_MAX_LENGTH = 50;

}
