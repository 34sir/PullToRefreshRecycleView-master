package com.example.pulltorefreshrecycleview.util;

import java.util.HashMap;

/**
 * Created by admin on 2017/4/12.
 */

public class ItemTypeUtil {
    private HashMap<Object, Integer> typePool;

    public void setTypePool(HashMap<Object, Integer> typePool) {
        this.typePool = typePool;
    }

    /**
     * @param type item的类型
     * @return 通过object类型的type来得到int类型的type
     */
    public int getIntType(Object type) {
        if (typePool == null) {
            typePool = new HashMap<>();
        }

        if (!typePool.containsKey(type)) {
            typePool.put(type, typePool.size());
        }
        return typePool.get(type);
    }
}
