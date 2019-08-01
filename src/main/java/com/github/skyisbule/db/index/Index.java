package com.github.skyisbule.db.index;

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

}
