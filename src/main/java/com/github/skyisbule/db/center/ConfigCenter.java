package com.github.skyisbule.db.center;

import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.util.JsonUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigCenter {

    private static Map<String, Db> dbInfo = new HashMap<>();

    public static Db getDbByName(String name){
        return dbInfo.get(name);
    }

    public static void getFromDisk() throws IOException {
        String path  = DefaultConfig.BASE_WORK_PATH;
        File target  = new File(path);
        File[] files = target.listFiles();
        if (files == null) return;
        for (File file : files) {
            String fileName = file.getName();
            String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
            if (file.isFile() && suffix.equals("config") ) {
                String json = new RandomAccessFile(file.getPath(),"rw").readLine();
                Db     db   = JsonUtil.fromJson(json,Db.class);
                dbInfo.put(db.getDbName(),db);
            }
        }
    }

    public static void createDB(String dbName) throws IOException {
        Db db = new Db();
        db.setDbName(dbName);
        String path = DefaultConfig.BASE_WORK_PATH;
        File file   = new File(path+dbName+".config");
        if (file.createNewFile()){
            String json = JsonUtil.getJson(db);
            RandomAccessFile writer = new RandomAccessFile(file,"rw");
            writer.writeBytes(json);
            dbInfo.put(dbName,db);
        }
    }

    public static void createTable(String dbName, Table table) throws IOException {
        Db db = dbInfo.get(dbName);
        db.getTables().put(table.tableName,table);
        String json = JsonUtil.getJson(db);
        RandomAccessFile writer = new RandomAccessFile(DefaultConfig.BASE_WORK_PATH + dbName +".config","rw");
        writer.writeBytes(json);
    }

    public static void flushConfig(String dbName) {
        try {
            Db db = dbInfo.get(dbName);
            String json = JsonUtil.getJson(db);
            RandomAccessFile writer = new RandomAccessFile(DefaultConfig.BASE_WORK_PATH + dbName +".config","rw");
            writer.writeBytes(json);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("flush db info error");
        }
    }

}
