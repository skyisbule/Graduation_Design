package com.github.skyisbule.db;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.center.InstanceManager;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.data.DataObject;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;

public class SkyDB {

    public static SkyDB skyDB;

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

    public DataObject get(Class clazz, String version) {
        String dbName = clazz.getName();
        String fullName = dbName + "_" + version;
        if (dataObjectMap.containsKey(fullName)) {
            return dataObjectMap.get(fullName);
        } else {
            DataObject object = new DataObject(mainService, clazz, version);
            dataObjectMap.put(fullName, object);
            return object;
        }
    }

}
