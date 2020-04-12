package com.github.skyisbule.db;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;

public class SkyDB {

    private static SkyDB skyDB;

    private static final String VERSION = "1.0.0 - snapshot";

    private static final String TYPE_INTEGER = "java.lang.Integer";
    private static final String TYPE_STRING = "java.lang.String";

    private static Map<String, DataObject> dataObjectMap = new HashMap<>();

    private MainService mainService;

    public static SkyDB getInstance() {
        if (skyDB == null) {
            synchronized (SkyDB.class) {
                if (skyDB == null) {
                    skyDB = new SkyDB();
                }
            }
        }
        return skyDB;
    }

    public static SkyDB getInstance(String workPath) {
        DefaultConfig.BASE_WORK_PATH = workPath;
        if (skyDB == null) {
            synchronized (SkyDB.class) {
                if (skyDB == null) {
                    skyDB = new SkyDB();
                }
            }
        }
        return skyDB;
    }

    public SkyDB() {
        System.out.println("[skyDB info] welcome to skyDB version:" + VERSION);
        System.out.println("[skyDB info] db starting...");
        InstanceManager.addInstance("IOCenter", new IOCenter());
        mainService = new MainService();
        System.out.println("[skyDB info] db start success!");
    }

    public DataObject create(Class clazz, String version) {
        try {
            Db db = ConfigCenter.getDbByName(clazz.getName());
            if (db != null && db.getTableByName(version) != null) {
                System.err.println(
                    "[skyDB error] db '" + clazz.getName() + "_" + version + "' has exit,please check or delete it.");
                return null;
            }

            mainService.createDb(clazz.getName());
            ArrayList<String> names = new ArrayList<>();
            ArrayList<ColumnTypeEnum> types = new ArrayList<>();
            Table table = new Table();
            table.setTableName(version);

            for (Field field : clazz.getFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Type type = field.getType();
                names.add(fieldName);
                if (TYPE_INTEGER.equals(type.getTypeName())) {
                    types.add(ColumnTypeEnum.INT);
                } else if (TYPE_STRING.equals(type.getTypeName())) {
                    types.add(ColumnTypeEnum.STRING);
                } else {
                    throw new IllegalArgumentException(
                        "error for object type , your field should use Integer or String,and make field not public is"
                            + " ok too");
                }
            }
            mainService.createTable(clazz.getName(), version, names, types);
            System.out.println("[skyDB] create success: " + clazz.getName() + "_" + version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataObject object = new DataObject(mainService, clazz, version);
        dataObjectMap.put(clazz.getName() + "_" + version, object);
        return object;
    }

    public DataObject getOrCreate(Class clazz, String version) {
        String dbName = clazz.getName();
        String fullName = dbName + "_" + version;
        if (dataObjectMap.containsKey(fullName)) {
            return dataObjectMap.get(fullName);
        } else {
            Db db = ConfigCenter.getDbByName(dbName);
            if (db == null || db.getTableByName(version) == null) {
                return create(clazz, version);
            }
            DataObject object = new DataObject(mainService, clazz, version);
            dataObjectMap.put(fullName, object);
            return object;
        }
    }

    /**
     * 先从缓存里删掉
     * 再删库文件
     * 再删索引文件
     */
    public void dropIfExit(Class clazz, String version) {
        String dbName = clazz.getName();
        Map<String, Db> dbInfo = ConfigCenter.getDbInfo();
        if (dbInfo.get(dbName) == null) {
            return;
        }
        String targetFile = DefaultConfig.BASE_WORK_PATH + dbName + "_" + version;
        //只有一张表的情况下直接删库
        if (dbInfo.get(dbName).tables.size() == 1) {
            Table table = dbInfo.get(dbName).tables.get(version);
            if (table != null) {
                dbInfo.remove(dbName);
                File dbFile = new File(DefaultConfig.BASE_WORK_PATH + dbName + ".config");
                if (!dbFile.delete()) {
                    System.err.println("[skyDB error]delete db file error:" + targetFile);
                }
            }
        } else {
            //多张表的话 就只能采取保留策略
            dbInfo.get(dbName).tables.remove(version);
            ConfigCenter.flushConfig(dbName);
        }
        //接下来需要删除真实的文件

        File dataFile = new File(targetFile + ".db");
        if (dataFile.exists()) {
            if (!dataFile.delete()) {
                System.err.println("[skyDB error]delete data file error:" + targetFile);
            }
        }
        File indexFile = new File(targetFile + ".index");
        if (indexFile.exists()) {
            if (!indexFile.delete()) {
                System.err.println("[skyDB error]delete index file error:" + targetFile);
            }
        }
    }

}
