package com.wlz.server.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @package com.wlz.netty.server.web
 * @Date Created in 2019/2/24 0:13
 * @Author myzf
 */
@Controller
public class MonitorController {

    private final String pattern = "heart:monitor:*";

    @Autowired
    private StringRedisTemplate redisTemplate;


    @ResponseBody
    @RequestMapping("getData.do")
    public Map<String,Object> getData(){
        Map<String,Object> map = new HashMap<>();
        Set<String> keys = redisTemplate.keys(pattern);
        for(String  k : keys){
            List<String> pingList = redisTemplate.opsForList().range(k, 0, 9);
            if(!pingList.isEmpty()){
                Collections.reverse(pingList);
                int index = k.lastIndexOf(":");
                map.put(k.substring(index+1),pingList);
            }

        }

        return map;

    }


    @RequestMapping("monitor.do")
    public String monitor(){
        return "monitor";
    }
}
