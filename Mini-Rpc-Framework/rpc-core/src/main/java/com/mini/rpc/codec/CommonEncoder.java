package com.mini.rpc.codec;

import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.enumeration.PackageType;
import com.mini.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonEncoder extends MessageToByteEncoder {
    private static final Logger logger = LoggerFactory.getLogger(CommonEncoder.class);

    private static final int MAGIC_NUMBER = 0XCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer){
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        /**
         * 解码方法这里读入数据的顺序和写入数据的顺序是一样的
         * 先读取魔数 再读取package type 再读取序列器序号 再读取消息数据长度 再读取消息数组
         */


        //数据写到缓冲区
        out.writeInt(MAGIC_NUMBER);

        if(msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            out.writeInt(PackageType.RESPONSE_PACK.getCode());

        }

        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);

    }
}
