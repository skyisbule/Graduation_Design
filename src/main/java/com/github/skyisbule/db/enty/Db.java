package com.github.skyisbule.db.enty;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class Db {

    public String dbName;

    public Map<String,Table> tables = new HashMap<>();

    public Table getTableByName(String tableName){
        return tables.get(tableName);
    }

}
