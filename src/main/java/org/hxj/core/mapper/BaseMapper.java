package org.hxj.core.mapper;

import java.util.List;

public interface BaseMapper<T>{

    T selectById(Object id);

    List<T> selectByParam(T t);

}
