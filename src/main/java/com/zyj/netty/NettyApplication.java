package com.zyj.netty;

import com.zyj.netty.server.HttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author:77
 * @date: 2020/3/3 0003
 * @time: 13:32
 */
@SpringBootApplication
public class NettyApplication {
    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
        HttpServer httpServer = new HttpServer();
        httpServer.start();
    }

}
