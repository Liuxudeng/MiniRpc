package com.mini.rpc.socket.util;

import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.PackageType;
import com.mini.rpc.enumeration.RpcError;
import com.mini.rpc.exception.RpcException;
import com.mini.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 通过socket方式从输入流获取字节并反序列化
 */
public class ObjectReader {

    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);

    private static final int MAGIC_NUMBER = 0XCAFEBABE;


    public static Object readObject(InputStream in) throws IOException {
        byte[] numberBytes = new byte[4];

        /**
         * 判断魔数
         */
        in.read(numberBytes);
        int magic = bytesToInt(numberBytes);

        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包：{}", magic);

            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
/**
 * 判断packageType类型
 */
        in.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;

        }else if(packageCode==PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else{
            logger.error("不识别的数据包：{}",packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }

        /**
         * 判断序列化器类型
         */

        in.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);

       CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);

       if(serializer==null){
           logger.error("不识别的反序列化器:{}",serializerCode);
           throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
       }

        /**
         * 获取数据长度
         */

        in.read(numberBytes);
        int length = bytesToInt(numberBytes);
        byte[] bytes = new byte[length];
        in.read(bytes);
        return serializer.deserialize(bytes, packageClass);


    }


    /**
     * 将字节数组转换成Int
     */

    private static int bytesToInt(byte[] src) {
        int value;
        value = (src[0] & 0XFF) | ((src[1] & 0xFF) << 8)
                | ((src[2] & 0xFF) << 16)
                | ((src[3] & 0xFF) << 24);

        return value;
    }


}
