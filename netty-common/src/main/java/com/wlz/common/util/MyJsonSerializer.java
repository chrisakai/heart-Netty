package com.wlz.common.util;

import com.alibaba.fastjson.JSON;

/**
 * @Author Apollo
 * @Date 2023/9/18 11:30
 * @Version 1.0
 */
public class MyJsonSerializer implements MySerializer{
    @Override
    public byte[] serialize(Object object){
        return JSON.toJSONBytes(object);
    }

    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        return JSON.parseObject(bytes, clazz);
    }
}
