package com.mini.rpc.codec;


import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.PackageType;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);

    //设置一个魔数 表示协议包
    private static final int MAGIC_NUMBER = 0XCAFEBABE;

    /**
     * 该解码方法将会核对 MagicNumber ,PackageType ,SerializerType,
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * 注意这里写入数据的过程
         *
         * 先是写入MAGIC_NUMBER 再写入package type再写入序列化器编号 再写入传输消息数组的长度 最后写入具体的消息字节数组
         */


        //从缓冲区读取数据

        int magic = in.readInt();

        if(magic!=MAGIC_NUMBER){
            logger.error("不识别的协议包:{}",magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }

        int packageCode = in.readInt();
        Class<?> packageClass;
        if(packageCode== PackageType.REQUEST_PACK.getCode()){
            //如果packageType是一个调用请求
            packageClass = RpcRequest.class;
        }else if(packageCode==PackageType.RESPONSE_PACK.getCode()){
            //如果packType是一个响应请求
            packageClass = RpcResponse.class;
        }else{
            //packType既不是响应也不是请求。都不是的情况
            logger.error("不识别的数据包：{}",packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);

        if(serializer==null){
            //如果序列化器编号为空
            logger.error("未识别的反序列化器：{}",serializerCode);
            throw  new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }

        //获取数据

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes,packageClass);

        //添加到对象

        out.add(obj);



    }
}
