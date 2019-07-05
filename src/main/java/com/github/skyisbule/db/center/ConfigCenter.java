package com.github.skyisbule.db.center;

import com.github.skyisbule.db.enty.Db;

import java.util.HashMap;
import java.util.Map;

public class ConfigCenter {

    public static Map<String, Db> dbInfo = new HashMap<>();

    public static Db getDbByName(String name){
        return dbInfo.get(name);
    }

}
