package com.wlz.common.util;

/**
 * @Author Apollo
 * @Date 2023/9/18 11:28
 * @Version 1.0
 */
public class SerializerFactory {
    public static MySerializer getSerializer(byte serName){
        if(serName == Serialize.JSON){
            return new MyJsonSerializer();
        }
        return null;
    }
}
