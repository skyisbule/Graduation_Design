package com.github.skyisbule.db.context;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 计算上下文 在用户进行运算时可以将中间结果 计算结果塞进去
 * 诸如统计 xx 字段的个数
 */
@Getter
@Setter
public class ComputeContext {

    private List<Object> resultList = new ArrayList<>();

}
