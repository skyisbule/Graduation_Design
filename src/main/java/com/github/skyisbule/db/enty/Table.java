package com.github.skyisbule.db.enty;

import com.github.skyisbule.db.common.ColumnTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class Table {

    public String tableName;
    public int    pageNum;
    public int    recordNum;

    public List<ColumnTypeEnum> types;
    public List<String>         columnNames;

}
