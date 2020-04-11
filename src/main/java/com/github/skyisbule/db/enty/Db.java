package com.github.skyisbule.db.enty;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Db {

    public String dbName;

    public Map<String,Table> tables = new HashMap<>();

    public Table getTableByName(String tableName){
        return tables.get(tableName);
    }

}
