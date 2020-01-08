package com.github.skyisbule.db.parser;

import java.util.List;

import com.github.skyisbule.db.context.ParseContext;

/**
 * 统一的文件解析接口，用来解析文件，返回解析后的可使用的对象
 */
public interface Parser {

    List<String> doParser(String line, ParseContext context);

}
