package com.github.skyisbule.db.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 文件解析的上下文
 */
@Getter
@Setter
public class ParseContext {

    List<String> tempList = new ArrayList<>();

    Map<String, Object> tempMap = new HashMap<>();

    Long tempCount = 0L;

    public void addCount(long val) {
        tempCount += val;
    }

}
