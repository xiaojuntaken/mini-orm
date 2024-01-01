package org.hxj.factory;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @authorxiaojun
 * @date2023/12/31 15:27
 */
@Slf4j
public class DynamicMapperProxy implements InvocationHandler {

    //method调用的方法
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //因为debug时候发现动态代理类出现null现象
        if(Object.class.equals(method.getDeclaringClass())){
            return method.invoke(this,args);
        }
        System.out.println("method:"+method.getName());
        return null;
    }
}
