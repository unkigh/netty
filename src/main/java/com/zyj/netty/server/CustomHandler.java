package com.zyj.netty.server;

import com.alibaba.fastjson.JSON;
import com.oracle.deploy.update.Updater;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author:77
 * @date: 2020/3/3 0003
 * @time: 10:28
 */
@Slf4j
public class CustomHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
//    @Override
//    protected void messageReceived(ChannelHandlerContext context, HttpObject http) throws Exception {
//        Channel channel = context.channel();
//
//        if(http instanceof )
//    } //FullHttpRequest

    private String type = "text/plain";
    private int dataLen = 0;
    private byte[] act,plVsn,token,random,checkValue;

    @Override
    protected void messageReceived(ChannelHandlerContext context, FullHttpRequest request) throws Exception {
//     通过context上下文获取Channel
        Channel channel = context.channel();
        //获取请求地址
        SocketAddress url = channel.remoteAddress();
        System.out.println("request.url ==>" + url);

        //自定义相应客户端信息
        byte[] bytes = getRequestParams(request);
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);

        //构建httpResponse对象
        FullHttpResponse resp = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                byteBuf
        );

//        设置responese对象的头信息
        HttpHeaders headers = resp.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
        headers.setInt(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

//        将数据刷到客户端
        context.writeAndFlush(resp);
    }

    private byte[] getRequestParams(FullHttpRequest request) {
        HttpHeaders headers = request.headers();
        List<Map.Entry<CharSequence, CharSequence>> entries = headers.entries();
        Map map = new HashMap();
        entries.forEach(item->{
            map.put(item.getKey(), item.getValue());
        });
        Map reMap = new HashMap();
        try {
            act = (byte[]) map.get("ACT");
            if(getValidLength(act) != 1 ) {
                return JSON.toJSONBytes(Response.success("ACT 值异常：" + act));
            }
            boolean ac = EnumAct.SEL_STOCK == JSON.toJSONString(act) ? true : false;
            log.info("ACT：" + act + "  " + ac);
            if(EnumAct.LOGIN == byteToString(act)){
                selectStock(map);
            }
        } catch (Exception e) {
            log.warn("请求参数不正确 ===》" + e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        }

        try {
//            reMap.put("DATA_LEN", dataLen);
//            reMap.put("ACT", JSON.toJSONString(act));
//            reMap.put("PL_VSN", JSON.toJSONString(plVsn));
//            reMap.put("TOKEN", JSON.toJSONString(token));
//            reMap.put("RANDOM", JSON.toJSONString(random));
//            reMap.put("TERMINAL_NUM", )
        }catch (Exception e) {
            log.warn("返回报文构建失败 ===>" + e.getMessage());
            throw new IllegalStateException(e.getMessage(), e);
        }
        return JSON.toJSONBytes(map);
    }

    private byte[] selectStock(Map map) {
        dataLen = (int) map.get("DATA_LEN");
        plVsn = (byte[])map.get("PL_VSN");
        token = (byte[])map.get("TOKEN");
        random = (byte[])map.get("RANDOM");
        checkValue = (byte[])map.get("CHECK_VALUE");

        if(dataLen < 10 || dataLen > 99) {
            return JSON.toJSONBytes(Response.success("DATA_LEN 值异常：" + dataLen));
        }
        if(getValidLength(plVsn) != 1) {
            return JSON.toJSONBytes(Response.error("PL_VSN 值异常：" + plVsn));
        }
        if(getValidLength(token) != 4) {
            return JSON.toJSONBytes(Response.error("TOKEN 值异常：" + token));
        }
        if(getValidLength(random) != 4) {
            return JSON.toJSONBytes(Response.error("RANDOM 值异常：" + random));
        }
        if(getValidLength(checkValue) != 4) {
            return JSON.toJSONBytes(Response.error("CHECK_VALUE 值异常：" + checkValue));
        }
        return JSON.toJSONBytes(Response.success("ok"));
    }

    private String byteToString(byte[] bytes) {
        return JSON.toJSONString(bytes);
    }

    /**
     * 获取byte的实际长度
     * @param bytes
     * @return
     */
    public int getValidLength(byte[] bytes){
        int i = 0;
        if (null == bytes || 0 == bytes.length) {
            return i;
        }
        for (; i < bytes.length; i++) {
            if (bytes[i] == '\0') {
                break;
            }
        }
        return i + 1;
    }


}
