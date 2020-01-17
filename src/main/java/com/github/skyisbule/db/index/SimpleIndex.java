package com.github.skyisbule.db.index;

import java.util.ArrayList;
import java.util.List;

import com.github.skyisbule.db.enty.Page;

/**
 * 简单索引实现，底层会存储每个页表的maxid和minid并缓存进内存
 * 之后查找的话就可以实现接近
 */
public class SimpleIndex implements Index{

    private List<Page> pageList = new ArrayList<>();

    @Override
    public boolean sync() {
        return false;
    }

    @Override
    public void initFromDisk() {

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
    public void addIndex(int id) {

    }

    @Override
    public void delIndex(int id) {

    }

    @Override
    public void flush(Page page) {

    }

    @Override
    public void flush(List<Page> pages) {

    }

    @Override
    public Index getInstance(String db, String table) {
        return null;
    }


}
