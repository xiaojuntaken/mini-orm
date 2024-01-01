package org.hxj.core.mapper;

public interface BaseMapper<T>{

    T selectById(Object id);

    T selectByParam(T t);

}
