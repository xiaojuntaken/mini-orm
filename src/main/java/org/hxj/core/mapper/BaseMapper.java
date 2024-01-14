package org.hxj.core.mapper;

import java.io.Serializable;
import java.util.List;

public interface BaseMapper<T> {

    T selectById(Object id);

    List<T> selectByParam(T t);

    int deleteById(Serializable id);

    int updateById(T t);

}
