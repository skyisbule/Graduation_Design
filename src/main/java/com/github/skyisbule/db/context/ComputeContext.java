package com.github.skyisbule.db.context;

import java.util.ArrayList;
import java.util.List;

import com.github.skyisbule.db.data.DataObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 计算上下文 在用户进行运算时可以将中间结果 计算结果塞进去
 * 诸如统计 xx 字段的个数
 */
@Getter
@Setter
public class ComputeContext {

    private List<Object> resultList = new ArrayList<>();

    private List<DataObject> dataObjects = new ArrayList<>();

    public int nowPage = 1;

    private int totalPage;

    private boolean pageChangeFlag = false;

    private boolean forceExit = false;

    public void forceExit(){
        this.forceExit = true;
    }

    private void setNowPage(int page){
        pageChangeFlag = true;
        nowPage = page;
    }

}
