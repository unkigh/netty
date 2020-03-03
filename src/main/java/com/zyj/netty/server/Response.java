package com.zyj.netty.server;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 表示RPC的返回
 * @author:77
 * @date: 2020/2/26 0026
 * @time: 11:33
 */
@Data
@AllArgsConstructor
public class Response {
    /**
     * 服务返回编码，0-成功，非0失败
     */
    private int code = 0;
    /**
     * 具体错误信息
     */
    private String msg = "ok";

    private Object data;


    public static Response success(String msg){
        return new Response(0, msg, null);
    }

    public static Response success(String msg, Object data){
        return new Response(0, msg, data);
    }

    public static Response error(String msg){
        return new Response(1, msg, null);
    }

    public static Response error(String msg, Object data){
        return new Response(0, msg, data);
    }

}
