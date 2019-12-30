package com.github.skyisbule.db.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.skyisbule.db.MainService;
import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.util.JsonUtil;
import com.google.gson.JsonObject;

public class DataObject {

    private MainService mainService;

    private String dbName;
    private String tableName;

    private Class clazz;

    public DataObject(MainService mainService, Class clazz, String tableName) {
        this.mainService = mainService;
        this.clazz = clazz;
        this.dbName = clazz.getName();
        this.tableName = tableName;
    }

    public void doInsert(Object object) {
        try {
            List<String> record = new ArrayList<>();
            for (Field field : clazz.getFields()) {
                field.setAccessible(true);
                record.add(field.get(object).toString());
            }
            mainService.doInsert(dbName, tableName, record);
        } catch (Exception e) {
            System.err.println("[skyDB error]insert data error , please check stack :");
            e.printStackTrace();
        }
    }

    public List<Object> getPage(int page) throws IllegalAccessException, InstantiationException {
        List<Object> result = new LinkedList<>();
        LinkedList<ArrayList<String>> records = mainService.doSelect(dbName, tableName, page);
        Db db = ConfigCenter.getDbByName(dbName);
        Table table = db.getTableByName(tableName);
        List<ColumnTypeEnum> types = table.getTypes();

        for (ArrayList<String> record : records) {
            Object object = clazz.newInstance();
            for (int i = 0; i < types.size(); i++) {
                Field field = clazz.getFields()[i];
                field.setAccessible(true);
                ColumnTypeEnum typeEnum = types.get(i);
                switch (typeEnum){
                    case STRING:
                        field.set(object,record.get(i));
                        break;
                    case INT:

                    case TIME:
                        field.set(object,Integer.parseInt(record.get(i)));
                        break;
                }

            }
            result.add(object);
        }
        return result;
    }

}
