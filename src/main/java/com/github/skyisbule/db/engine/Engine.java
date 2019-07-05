package com.github.skyisbule.db.engine;

import com.github.skyisbule.db.center.ConfigCenter;
import com.github.skyisbule.db.common.ColumnTypeEnum;
import com.github.skyisbule.db.common.DefaultConfig;
import com.github.skyisbule.db.enty.Db;
import com.github.skyisbule.db.enty.Page;
import com.github.skyisbule.db.enty.Table;
import com.github.skyisbule.db.io.IOCenter;
import com.github.skyisbule.db.util.ByteUtil;

import java.util.List;

public class Engine {

    //todo 这里未来改成从 instance center获取
    private IOCenter ioCenter = new IOCenter();

    public List<String> doSelect(){
        return null;
    }

    public boolean doInsert(String dbName,String tableName,List<String> columns){
        Db    db    = ConfigCenter.getDbByName(dbName);
        Table table = db.getTableByName(tableName);
        int pageNum = table.getPageNum();

        Page page   = ioCenter.getPage(dbName,tableName,pageNum);
        List<ColumnTypeEnum> types = table.getTypes();
        //开始构建字节序列
        byte[] data = new byte[0];
        for (int i = 0; i < types.size(); i++) {
            switch (types.get(i)){
                case TIME://跟INT处理方式一样
                case INT://直接往后续   int类型末尾不需要终止位  因为我们知道它一定是4个字节
                    byte[] num;
                    if (columns.get(i) == null)
                        num    = ByteUtil.getDataNull();
                    else
                        num    = ByteUtil.int2byte(Integer.parseInt(columns.get(i)));
                    data       = ByteUtil.byteMerger(data,num);
                    break;
                case STRING:
                    byte[] strBytes;
                    String str = columns.get(i);
                    if (str == null)
                        strBytes = ByteUtil.getDataNull();
                    else
                        strBytes = str.getBytes();
                    data = ByteUtil.byteMerger(data,strBytes);
                    byte[] columnEnd = ByteUtil.getColumnEnd();
                    data = ByteUtil.byteMerger(data,columnEnd);
                    break;
            }
        }
        //拼接一下这条数据有没有被删除
        data    = ByteUtil.byteMerger(data,ByteUtil.int2byte(DefaultConfig.ALIVE_FLAG));
        int len = data.length;
        //构建完成  此时data  已经为记录数据+换行符的格式了
        //接下来要判断当前页表能否有足够空间供我们插入数据  如果没有的话需要新建页表
        byte[] source  = page.data;
        int pageEndPos = page.getPageEndPos();
        if (pageEndPos + len < (1024*16 - 16 - 1)){ //代表此时不需要分页,直接在页末添加数据
            int    newMaxPos     = pageEndPos + len;               //0
            byte[] endPosHeader  = ByteUtil.int2byte(newMaxPos);  //1
            int    newRecordId   = page.maxId + 1;
            source = ByteUtil.write(source,ByteUtil.int2byte(newRecordId),8);
            source = ByteUtil.write(source,endPosHeader,12); //2 这3行更新header头信息 maxPos 和 maxId
            page.setData(ByteUtil.write(source,data,pageEndPos));//数据写入source
        }else{
            Page newPage = new Page();
            newPage.setPageNum(page.getPageNum() + 1);
            newPage.setMinId(page.maxId + 1);
            newPage.setMaxId(page.maxId + 1);
            newPage.setPageEndPos(16 + len);
            byte[] newData = getEmptyPage(page.pageNum+1,page.minId+1,data);
            newPage.setData(newData);
        }
        return ioCenter.writePage(dbName,tableName,page);
    }

    private byte[] getEmptyPage(int pageId,int indexId,byte[] data){
        byte[] pageIdBytes = ByteUtil.int2byte(pageId);
        byte[] pageIndexId = ByteUtil.int2byte(indexId);
        byte[] pageEndPos  = ByteUtil.int2byte(data.length + 16);
        byte[] header      = new byte[0];
        header = ByteUtil.byteMerger(header,pageIdBytes);
        header = ByteUtil.byteMerger(header,pageIndexId);
        header = ByteUtil.byteMerger(header,pageIndexId);
        header = ByteUtil.byteMerger(header,pageEndPos);
        //build header end   =>>>>   start build record
        byte[] result = new byte[0];
        result = ByteUtil.byteMerger(result,header);
        result = ByteUtil.byteMerger(result,data);
        return result;
    }

}
