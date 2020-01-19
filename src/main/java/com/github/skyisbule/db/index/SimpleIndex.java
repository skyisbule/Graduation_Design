package com.github.skyisbule.db.index;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.util.ArrayList;
import java.util.List;

import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.enty.Page;
import com.github.skyisbule.db.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

/**
 * 简单索引实现，底层会存储每个页表的maxid和minid并缓存进内存
 * 之后查找的话就可以实现接近
 */
public class SimpleIndex implements Index {

    private String dbName;
    private String tableName;
    private String targetFile;

    private List<Page> pageList = new ArrayList<>();

    private static final int MIN_FIND_SIZE = 10;

    @Override
    public boolean sync() {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initFromDisk() {
        File file = new File(targetFile);
        try {
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String json = reader.readLine();
                pageList = JsonUtil.getGson().fromJson(json, new TypeToken<List<Page>>() {}.getType());
            } else {
                if (!file.createNewFile()) {
                    throw new IOError(new Throwable("create file error :" + targetFile));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean createIndex() {
        return false;
    }

    //采取少数据量线性查找，大量数据量二分查找的策略，最大化的保障效率。
    @Override
    public int getPageNum(int id) {
        if (pageList.size() < MIN_FIND_SIZE) {
            return linearSearch(id, pageList, 0, pageList.size());
        }
        //代表索引已经很大了，线性查找很亏，所以从这里开始走二分查找。
        int min = 0;
        int max = pageList.size();
        while (true) {
            int medium = (min + max) / 2;
            Page page = pageList.get(medium);
            if (id >= page.getMinId() && id <= page.getMaxId()) {
                return page.getPageNum();
            }
            if (id < page.getMinId()) {
                max = medium;
            } else {//代表id一定在这个页表的后边
                min = medium;
            }
            if (max - min < MIN_FIND_SIZE) {
                return linearSearch(id, pageList, min, max);
            }
        }
    }

    private int linearSearch(int id, List<Page> pages, int begin, int end) {
        for (int i = begin; i < end; i++) {
            Page page = pages.get(i);
            if (page.getMinId() <= id && page.getMaxId() >= id) {
                return page.getPageNum();
            }
        }
        System.out.println(
            "[skyDB info]try find index error,nothing match,please check your operation for " + targetFile + " "
                + id);
        return 0;
    }

    @Override
    public void addIndex(Page page) {
        //至于为什么又拷贝了一份呢，主要是考虑到这里传进来的page对象其实是带data的，是个体积非常大的对象，所以给他去掉
        //但为什么不直接page.setData(null)呢？ 主要还是担心这个对象外边指不定还会用，直接丢掉的话会留暗坑
        Page indexPage = new Page();
        indexPage.setMaxId(page.getMaxId());
        indexPage.setMinId(page.getMinId());
        indexPage.setPageNum(page.getPageNum());
        pageList.add(indexPage);
        flush();
    }

    @Override
    public void addIndex(List<Page> pages) {
        //为什么这么做看上边
        for (Page page : pages) {
            Page indexPage = new Page();
            indexPage.setMaxId(page.getMaxId());
            indexPage.setMinId(page.getMinId());
            indexPage.setPageNum(page.getPageNum());
            pageList.add(indexPage);
        }
        flush();
    }

    @Override
    public void delIndex(int id) {

    }

    public void flush() {
        File file = new File(targetFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String json = JsonUtil.getJson(pageList);
            writer.write(json);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void flush(Page page) {

    }

    @Override
    public void flush(List<Page> pages) {

    }

    @Override
    public Index getInstance(String db, String table) {
        SimpleIndex index = new SimpleIndex();
        index.dbName = db;
        index.tableName = table;
        index.targetFile = DefaultConfig.BASE_WORK_PATH + db + "_" + table + ".index";
        index.initFromDisk();
        return index;
    }

}
