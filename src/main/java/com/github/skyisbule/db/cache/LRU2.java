package com.github.skyisbule.db.cache;

import com.github.skyisbule.db.enty.Page;

import java.util.HashMap;
import java.util.LinkedList;

/**
 *  设计思想
 *  使用新生代，老年代和永久代
 *  新生代为一个LRU缓存 与正常的LRU缓存并无特殊的地方
 *  但它会记录每一个节点的存活时间（即每次发生访问时都会将年龄+1
 *  当存活时间达到一定次数时他就会进入老年代，当老年代满 则将老年代里的存活时间
 *  小于晋升对象的对象移除掉
 *  永久代则是一个不参与淘汰和计算的空间
 */
public class LRU2 {

    public LRU2(){

    }

    private int capacity;

    private HashMap<Integer,Page> edenMap;
    private HashMap<Integer,Page> edenCount;

    private HashMap<Integer,Page> oldMap;
    private HashMap<Integer,Page> oldCount;

    private HashMap<Integer,Page> perm;


    public void add(Integer id,Page page){
        //直接插入
        if (edenMap.size()<capacity){
            edenMap.put(id,page);
            //edenCount.put()
        }
    }

}
