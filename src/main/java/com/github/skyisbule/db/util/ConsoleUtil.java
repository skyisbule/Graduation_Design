package com.github.skyisbule.db.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.github.skyisbule.db.common.DefaultConfig;

public class ConsoleUtil {

    public static void show(LinkedList<ArrayList<String>> data) {
        if (data == null || data.size() == 0) {
            System.out.println("[ConsoleUtil - show] empty data list , nothing to show");
            return;
        }
        int columnNum = data.get(0).size();
        ArrayList<Integer> lengthList = new ArrayList<>(columnNum);
        for (int i = 0; i < columnNum; i++) {
            lengthList.add(0);
        }
        for (List<String> row : data) {
            for (int i = 0; i < row.size(); i++) {
                if (lengthList.get(i) < row.get(i).length()) { lengthList.set(i, row.get(i).length()); }
            }
        }
        int totalLen = 0;
        for (Integer len : lengthList) {
            totalLen += len;
        }
        if (totalLen > columnNum * DefaultConfig.COLUMN_MAX_LENGTH) {
            totalLen = columnNum * DefaultConfig.COLUMN_MAX_LENGTH;
        }
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < totalLen + columnNum + 1; i++) {
            line.append('-');
        }
        String outLine = line.toString();
        System.out.println(outLine);
        int index;
        for (List<String> row : data) {
            index = 0;
            line.delete(0, line.length());
            line.append('|');
            for (String column : row) {
                int max = lengthList.get(index);
                line.append(column);
                for (int i = 0; i < (max - computeLength(column)); i++) {
                    line.append(' ');
                }
                line.append('|');
                index++;
            }
            System.out.println(line.toString());
        }
        System.out.println(outLine);
    }

    private static int computeLength(String str) {
        return str.length();
    }

    public static void show(List<Object> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        LinkedList<ArrayList<String>> param = new LinkedList<>();
        try {
            Object info = list.get(0);
            Field[] fields = info.getClass().getFields();

            for (Object object : list) {
                ArrayList<String> record = new ArrayList<>();
                for (Field field : fields) {
                    record.add(field.get(object).toString());
                }
                param.add(record);
            }
            show(param);
        }catch (Exception e){
            System.err.println("[skyDB error] error for show info ,please check your param.");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        LinkedList<ArrayList<String>> list = new LinkedList<>();
        ArrayList<String> row = new ArrayList<>();
        row.add("skyisbule");
        row.add("man");
        list.add(row);
        show(list);

    }

}
