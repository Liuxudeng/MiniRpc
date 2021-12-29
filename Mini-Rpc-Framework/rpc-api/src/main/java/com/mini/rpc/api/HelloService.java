package com.mini.rpc.api;

/**
 * 通用接口  注意这个接口的实现在服务端
 */
public interface HelloService {
    /**
     * hello方法要传递一个对象，这个对象需要实现Serializable接口，因为他需要在调用过程中冲客户端传递给服务端，必须进行序列化
     */

String hello(HelloObject object);
}
