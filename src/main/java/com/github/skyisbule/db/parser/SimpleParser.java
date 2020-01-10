package com.github.skyisbule.db.parser;

import java.util.Arrays;
import java.util.List;

import com.github.skyisbule.db.context.ParseContext;

/**
 * 内置的一个简单的文件解析器
 */
public class SimpleParser implements Parser {

    public SimpleParser(String separator) {
        this.separator = separator;
    }

    /**
     * 代表 line 分隔符格式的结构 比如：
     * id name age
     * -----------以下为文本内的数据，则在构造parser的时候传入" "即可
     * 1 sky 23
     * 2 sky 23
     */
    private String separator;

    @Override
    public List<String> doParser(String line, ParseContext context) {
        return Arrays.asList(line.split(separator));
    }

}
