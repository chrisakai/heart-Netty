package com.wlz.common.util;

/**
 * @Author Apollo
 * @Date 2023/9/18 11:24
 * @Version 1.0
 */
public interface MySerializer {
    //Java对象转化为二进制字节流
    byte[] serialize(Object object);
    //二进制转换成java对象
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
