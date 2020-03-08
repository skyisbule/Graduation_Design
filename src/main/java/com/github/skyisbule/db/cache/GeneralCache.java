package com.github.skyisbule.db.cache;

public class GeneralCache {

    private GeneralCache() {}

    private static volatile GeneralCache generalCache;

    public static GeneralCache getInstance() {
        if (generalCache == null) {
            synchronized (GeneralCache.class) {
                if (generalCache == null) {
                    generalCache = new GeneralCache();
                }
            }
        }
        return generalCache;
    }

}
