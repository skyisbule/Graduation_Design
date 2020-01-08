package com.github.skyisbule.db.parser;

import java.util.Arrays;
import java.util.List;

import com.github.skyisbule.db.context.ParseContext;

/**
 * 内置的一个简单的文件解析器
 */
public class SimpleParser implements Parser {

    private static final String BLANK_SPILT = " ";

    @Override
    public List<String> doParser(String line, ParseContext context) {
        List<String> columns = Arrays.asList(line.split(BLANK_SPILT));
        return columns;
    }

}
