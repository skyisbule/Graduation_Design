package com.github.skyisbule.db.index;

import java.util.List;

import com.github.skyisbule.db.enty.Page;

public interface Index {

    //将索引同步到文件
    public boolean sync();

    //从索引文件中初始化内容至内存
    public void initFromDisk();

    //初始化索引
    public boolean createIndex();

    //通过主键id拿到记录所属页号
    public int getPageNum(int id);

    //添加索引
    public void addIndex(int id);

    //删除索引
    public void delIndex(int id);

    //把索引刷新进文件
    public void flush(Page page);

    public void flush(List<Page> pages);

    //获取实例，如果没有的话，实现类应该创建一个
    public Index getInstance(String db, String table);

}
