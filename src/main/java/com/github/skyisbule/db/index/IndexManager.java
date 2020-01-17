package com.github.skyisbule.db.index;

import java.util.HashMap;
import java.util.Map;

import com.github.skyisbule.db.data.DataObject;

/**
 * 暴露给外界获取索引实例的管理器
 */
public class IndexManager {

    private static Index simpleIndex = new SimpleIndex();

    private static Map<String, Index> indexCache = new HashMap<>();

    public static Index getIndex(String key, String version) {
        if (indexCache.containsKey(getKey(key, version))) {
            return indexCache.get(getKey(key, version));
        } else {
            Index index = simpleIndex.getInstance(key, version);
            indexCache.put(getKey(key, version), index);
            return index;
        }
    }

    public static void getIndex(DataObject dataObject) {
        getIndex(dataObject.getDbName(), dataObject.getTableName());
    }

    private static String getKey(String key, String version) {
        return key + "_" + version;
    }

}
