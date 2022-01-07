package com.mini.rpc.entity;

import com.mini.rpc.enumeration.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 这个类包含的信息是服务端给客户端的响应
 * @param <T>
 */

@AllArgsConstructor
@NoArgsConstructor

@Data
public class RpcResponse <T> implements Serializable {
    /**
     * 响应对应的请求号
     */

    private String requestId;


    /**
     * 响应状态码
     */

    private Integer statusCode;


    /**
     * 响应状态码对应的信息
     */

    private String message;

    /**
     * 成功时的响应数据  因为响应数据多种多样 所以要用泛型
     */

    private T data;


    /**
     * @description 成功时服务端返回的对象
     * @param data  成功时的数据
     * @param <T>
     * @return  返回响应成功的数据
     */

    public static <T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> response = new RpcResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getCode());

        response.setData(data);
            return response;
    }


    /**
     * @description 失败时服务端返回的对象
     * @param code 失败时的状态码
     * @param <T>  返回的信息
     * @return  失败时返回的信息
     */
    public static <T> RpcResponse<T> fail(ResponseCode code,String requestId){
        RpcResponse<T> response = new RpcResponse<>();

        response.setRequestId(requestId);
        response.setStatusCode(code.getCode());

        response.setMessage(code.getMessage());

        return response;
    }



}
