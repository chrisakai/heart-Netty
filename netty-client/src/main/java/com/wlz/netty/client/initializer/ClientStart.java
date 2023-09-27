package com.wlz.netty.client.initializer;

import com.wlz.netty.client.heart.NettyClient;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;


public class ClientStart implements ApplicationContextInitializer {




    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        start();
    }

    private void start() {
        try {
            NettyClient client = new NettyClient();
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
