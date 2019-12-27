package com.github.skyisbule.db.compute;

/**
 * 计算模型的统一接口
 * 使用者对数据操作的实现都会放在这里
 */
public interface Computer {

    void init();

    void doCompute();

    void afterOnePage();

    void afterAll();

    void onException();

}
