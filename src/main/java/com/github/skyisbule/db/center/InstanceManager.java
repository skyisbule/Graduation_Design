package com.github.skyisbule.db.center;

import java.util.HashMap;

public class InstanceManager {

    private static HashMap<String,Object> instance = new HashMap<>();

    public static void addInstance(String name,Object object){
        instance.put(name,object);
    }

    public static Object getInstance(String name){
        return instance.get(name);
    }

}
