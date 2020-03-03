package com.zyj.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author:77
 * @date: 2020/3/3 0003
 * @time: 9:28
 */
public class HttpServer {

    private int port;

    public HttpServer() {
        this(8010);
    }

    public HttpServer(int port) {
        this.port = port;
    }


    public void start() {

        //定义主线程组，用于接收客户端请求，但不做任何逻辑处理
        EventLoopGroup boosGroup = new NioEventLoopGroup();
        //从线程组，主线程组会把请求转交给该线程，由从线程组去处理事务
        NioEventLoopGroup wokerGroup = new NioEventLoopGroup();
        //创建netty服务器
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        //服务器绑定两个线程组，并设置相应的助手类 Handler
        serverBootstrap.group(boosGroup, wokerGroup)
                //设置NIO双向通道
                .channel(NioServerSocketChannel.class)
                //子处理器
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        //channel获取相应通道
                        ChannelPipeline pipeline = channel.pipeline();

                        //为通道增加相应的Handler, 可接为是拦截器，监听器，监听客户端建立连接的信息
//                    当请求到服务端，我们需要对输出到客户端的数据做编码处理
                        pipeline.addLast("httpCode", new HttpServerCodec())
                                .addLast("aggregator", new HttpObjectAggregator(1024 * 1024)) //相当于1M
                                .addLast("handler",new CustomHandler());
                    }

                });

        try {
            //启动server并绑定端口号
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //关闭监听的channel
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }finally {
            //关闭退出
            boosGroup.shutdownGracefully();
            wokerGroup.shutdownGracefully();
        }
    }


}
