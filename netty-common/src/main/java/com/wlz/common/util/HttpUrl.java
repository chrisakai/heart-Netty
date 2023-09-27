package com.wlz.common.util;

import static com.wlz.common.util.MyDecoder.get;

/**
 * @Author Apollo
 * @Date 2023/9/19 16:30
 * @Version 1.0
 */
public class HttpUrl {

    /**
     * 终端软件更新推送接口地址
     *
     * @return http://ip:port/pushUpdate
     */
    public static String pushUpdate() {
        return String.format("http://%s:%s%s", get("serverIp"), get("serverPort"), get("serverRoute"));
    }
}
