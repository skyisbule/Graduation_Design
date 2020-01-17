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

/**
 * 简单索引实现，底层会存储每个页表的maxid和minid并缓存进内存
 * 之后查找的话就可以实现接近
 */
public class SimpleIndex implements Index {

    private String dbName;
    private String tableName;
    private String targetFile;

    private List<Page> pageList = new ArrayList<>();

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
                this.pageList = JsonUtil.fromJson(json, pageList.getClass());
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

    @Override
    public int getPageNum(int id) {
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
    }

    @Override
    public void delIndex(int id) {

    }

    public void flush(){
        File file = new File(targetFile);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String json = JsonUtil.getJson(pageList);
            writer.write(json);
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
