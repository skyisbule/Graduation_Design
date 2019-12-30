package com.github.skyisbule.db.compute;

import com.github.skyisbule.db.context.ComputeContext;

/**
 * 计算模型的统一接口
 * 使用者对数据操作的实现都会放在这里
 */
public interface Computer<T> {

    void init(ComputeContext computeContext);

    void doCompute(T t);

    void afterOnePage();

    void afterAll();

    void onException();

}
