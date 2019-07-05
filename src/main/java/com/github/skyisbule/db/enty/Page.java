package com.github.skyisbule.db.enty;

import lombok.Data;

import java.util.List;

/**
 * page的结构
 * | header   |   data   |   data  |  data |pageEnd
 * | header : | pageId minId  maxId  pageEndPos     4byte for all
 * | data :   |column|columnEnd| column|columnEnd|   deleted|
 * | endFlag:    1 Byte ascii: 4 00000100
 * | columnEnd : 1 Byte ascii: 3 00000011
 * | data_null : 1Byte ascii:  0 00000000
 */
@Data
public class Page {

    public int pageNum;
    public int minId;
    public int maxId;
    public int pageEndPos; //截止到哪里是有数据的 按0开始的坐标算

    public byte[] header; //16byte
    public byte[] data;   // data包含header

    public List<Record> records;

}
