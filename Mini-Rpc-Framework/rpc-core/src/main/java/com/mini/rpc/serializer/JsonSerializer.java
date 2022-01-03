package com.mini.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 使用Json格式的序列化器
 */
public class JsonSerializer implements CommonSerializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    /**
     * 关于ObjectMapper类的一些说明：
     * ObjectMapper读写操作：使用ObjectMapper的readValue方法，可以把JSON字符串转换为Java对象，
     * 同样，使用writeValue方法，可以将Java对象序列化为JSON。
     *
     *
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        /**
         * 序列化的时候要把java对象转化为json 这里指定的json类型是byte数组
         */

        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生：{}",e.getMessage());
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        /**
         * 反序列化是将json转化为java对象 因为之前序列化的时候规定了序列化后为byte数组 因此这里的反序列化
         * 是针对byte数组的
         */

        try {
            Object obj = objectMapper.readValue(bytes,clazz);

            //如果反序列化后的java对象是RpcRequest类的实例，那么就执行handleRequest()方法
            if(obj instanceof RpcRequest){
                obj = handleRequest(obj);
            }

            return obj;


        } catch (IOException e) {

            logger.error("反序列化时有错误发生：{}",e.getMessage());
            e.printStackTrace();
            return null;
        }

    }

    /**
     * handleRequest() 方法的作用。用来辅助反序列化
     * @param obj
     * @return
     * @throws IOException
     * @description 就是在 RpcRequest 反序列化时，由于Paramters字段是 Object 数组类型，
     * 而在反序列化时，序列化器是根据字段类型进行反序列化，Object是一个十分模糊的类型，就会出现反序列化失败的现象，
     * 这时就需要 RpcRequest 中的另一个字段 ParamTypes 来获取到 Object 数组中的每个实例的实际类，
     *
     *
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0; i < rpcRequest.getParamTypes().length; i++) {
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if(! clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i]= objectMapper.readValue(bytes,clazz);
            }
        }

        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
