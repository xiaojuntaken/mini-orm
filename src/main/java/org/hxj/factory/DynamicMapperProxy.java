package org.hxj.factory;


import lombok.extern.slf4j.Slf4j;
import org.hxj.entity.po.Activity;
import org.hxj.enums.MethodEnum;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @authorxiaojun
 * @date2023/12/31 15:27
 */
@Slf4j
public class DynamicMapperProxy<T> implements InvocationHandler {
    //创建属性接受接口类
    private Class<T> interfaceClass;

    //动态代理创建对象时将将被代理的接口传入
    public DynamicMapperProxy(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    //被动态代理创建的bean，被依赖注入的时候
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //因为debug时候发现动态代理类出现null现象
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        String name = proxy.getClass().getName();
        System.out.println("method:" + method.getName());
        return new Activity();
    }

    public Object executeSql(Method method, Object[] args) {
        String methodName = method.getName();
        if (MethodEnum.SELECT_BY_ID.getMethodName().equals(methodName)) {
            //根据主键ID查询，返回实体类


        }

        return null;
    }
}
