package com.github.skyisbule.db.common;

public enum  DataSaveEnum {

    MEMORY,//所有的数据都会被存入内存 即便爆内存也不会落库
    DISK,  //所有的数据都会被扔进库里，不会在内存里存数据
    MIX    //数据会先存在内存里，达到一定数量后才会落库

}
