package com.github.skyisbule.db.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.skyisbule.db.MainService;
import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.compute.Computer;
import com.github.skyisbule.db.context.ComputeContext;
import com.github.skyisbule.db.context.ParseContext;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.parser.Parser;
import com.github.skyisbule.db.util.JsonUtil;

public class DataObject {

    private MainService mainService;

    private String dbName;
    private String tableName;

    private Class clazz;

    private ComputeContext context;

    private Db db;
    private Table table;

    public DataObject(MainService mainService, Class clazz, String tableName) {
        this.mainService = mainService;
        this.clazz = clazz;
        this.dbName = clazz.getName();
        this.tableName = tableName;
        db = ConfigCenter.getDbByName(dbName);
        table = db.getTableByName(tableName);
        context = new ComputeContext();
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

    public void bathInsert(List<Object> list){
        try {
            LinkedList<List<String>> records = new LinkedList<>();
            for (Object obj : list) {
                List<String> record = new ArrayList<>();
                for (Field field : clazz.getFields()) {
                    field.setAccessible(true);
                    record.add(field.get(obj).toString());
                }
                records.add(record);
            }
            mainService.batchInsert(dbName, tableName, records);
        }catch (Exception e){
            System.err.println("[skyDB error]bathInsert failed please check your data or dataObject...");
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
                switch (typeEnum) {
                    case STRING:
                        field.set(object, record.get(i));
                        break;
                    case INT:

                    case TIME:
                        field.set(object, Integer.parseInt(record.get(i)));
                        break;
                }

            }
            result.add(object);
        }
        return result;
    }

    public void showInfo() {
        System.out.println(JsonUtil.getJson(ConfigCenter.getDbByName(dbName).tables.get(tableName)));
    }

    public void showBrothers() {
        System.out.println(JsonUtil.getJson(db));
    }

    public void doCompute(Computer computer) throws InstantiationException, IllegalAccessException {
        int pageNum = table.pageNum;

        computer.init(context);
        context.setTotalPage(pageNum);
        int i;
        while (context.getNowPage() <= context.getTotalPage()) {
            i = context.getNowPage();
            List<Object> list = this.getPage(i);
            for (Object obj : list) {
                computer.doCompute(obj, context);
            }
            computer.afterOnePage(context);
            if (!context.isPageChangeFlag()) {
                context.nowPage = i + 1;
            }
            if (context.isForceExit()) {
                break;
            }
        }

        computer.afterAll(context);
    }

    public void fromFile(String path, Parser parser) {
        try {
            File file = new File(path);
            ParseContext context = new ParseContext();
            LinkedList<List<String>> records = new LinkedList<>();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(file)
                )
            );
            String line = reader.readLine();
            while (line != null) {
                records.add(parser.doParser(line, context));
                if (records.size() > DefaultConfig.FLUSH_DISK_FLAG_NUM) {
                    mainService.batchInsert(dbName, tableName, records);
                    records = new LinkedList<>();
                }
                line = reader.readLine();
            }
            mainService.batchInsert(dbName, tableName, records);
        } catch (Exception e) {
            System.err.println("[skyDB error]load data error please check your file data or parser");
            e.printStackTrace();
        }

    }

}
