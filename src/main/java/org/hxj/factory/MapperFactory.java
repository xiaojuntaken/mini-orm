package org.hxj.factory;

import lombok.Data;
import org.hxj.mapper.ActivityMapper;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author xiaojun
 * @date 2023/12/31 21:11
 */
@Data
public class MapperFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    //
    public MapperFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public T getObject() {
        InvocationHandler invocationHandler = new DynamicMapperProxy<>(interfaceClass);
        return (T) Proxy.newProxyInstance
                (interfaceClass.getClassLoader(), new Class[]{interfaceClass}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public boolean isSingleton() {
        return FactoryBean.super.isSingleton();
    }
//    }
}
