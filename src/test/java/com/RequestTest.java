package com;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author:77
 * @date: 2020/3/3 0003
 * @time: 15:38
 */
@Slf4j
public class RequestTest {
    int port = 8010;
    String host = "127.0.0.1";

    @Test
    public void testTo(){
        EventLoopGroup group = new NioEventLoopGroup();

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("httpCode", new HttpClientCodec());
                        }
                    });
        try {
            ChannelFuture future = b.connect(host, port).sync();
            future.channel().writeAndFlush("Hello Netty Server, I am a common client");
            future.channel().closeFuture().sync();
        }catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }finally {
            group.shutdownGracefully();
        }
    }

    @Test
    public void test(){

        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel arg0)
                                throws Exception {
                            System.out.println("client initChannel..");

//                            arg0.pipeline().addLast(clientHandler);
                        }
                    });
// 发起异步连接操作
            ChannelFuture f = b.connect(host, port).sync();

            log.info("客户端已经连接..");

//            clientHandler.sendMessage("发送数据测试1");
//
//            clientHandler.sendMessage("发送数据测试2");
//
//            clientHandler.sendMessage("发送数据测试3");

// 等待客户端链路关闭
            f.channel().closeFuture().sync();
        } catch (Exception e){
            throw new IllegalStateException(e.getMessage());
        }finally {
// 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }

    }


}
