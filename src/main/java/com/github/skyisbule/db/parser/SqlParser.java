package com.github.skyisbule.db.parser;

import java.util.List;

import com.github.skyisbule.db.context.ParseContext;

/**
 * 这个内置的解析器主要是用来解析通用的.sql文件格式的数据
 * 它仅会解析 insert into () xxx 这类格式的数据，其他数据均会被跳过。
 * 与此同时，它也会对insert语句做初步的检查，过滤掉一部分明显不符合规范的数据，譬如表是5个字段，但它尝试插入6个。
 */
public class SqlParser implements Parser {

    @Override
    public List<String> doParser(String line, ParseContext context) {
        return null;
    }

}
