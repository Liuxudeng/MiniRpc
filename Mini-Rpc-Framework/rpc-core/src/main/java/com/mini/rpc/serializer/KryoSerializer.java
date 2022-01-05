package com.mini.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.mini.rpc.entity.RpcRequest;
import com.mini.rpc.entity.RpcResponse;
import com.mini.rpc.enumeration.SerializerCode;
import com.mini.rpc.exception.SerializeException;
import com.sun.org.apache.xml.internal.serializer.OutputPropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * kryo序列化的类
 */
public class KryoSerializer implements CommonSerializer{
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    /**
     * 使用ThreadLocal初始化kryo 因为kryo中的output和input是线程不安全的  这和json序列化是不同的
     *
     */

    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
       Kryo kryo = new Kryo();

       //注册类
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        //循环引用检测
        kryo.setReferences(true);
        //不强制注册类 默认为false 若设置为true则要求涉及到的所有类都要注册，包括jdk中的比如Object

        kryo.setRegistrationRequired(false);

        return kryo;
    });


    /**
     * 重写序列化方法
     *
     * 序列化是把对象变成信息
     * @param obj
     * @return
     */
    @Override
    public byte[] serialize(Object obj) {


        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            Output output = new Output(byteArrayOutputStream);
            Kryo kryo = kryoThreadLocal.get();

            kryo.writeObject(output,obj);
            kryoThreadLocal.remove();
            return output.toBytes();

        }catch (Exception e){
            logger.error("序列化时有错误发生："+e);
            throw new SerializeException("序列化时有错误发生");

        }
    }

    /**
     * 反序列化方法
     *
     * 反序列化是将输入的信息变成对象
     * @param bytes
     * @param clazz
     * @return
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)
            ;Input input = new Input(byteArrayInputStream)){
            Kryo kryo =kryoThreadLocal.get();
            Object o = kryo.readObject(input,clazz);
            kryoThreadLocal.remove();
            return o;



        }catch (Exception e){
            logger.error("反序列化时有错误发生"+e);
            throw new SerializeException("反序列化时有错误发生");

        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
