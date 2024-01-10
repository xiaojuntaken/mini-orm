package org.hxj.entity.table;

import lombok.Data;

import java.util.List;

/**
 * @author xiaojun
 * @date 2024/1/6 10:27
 */
@Data
public class TableMetaData {

    private Class<?> tableClass;
    //表名
    private String tableName;
    //主键名
    private String pkColumnName;

    private List<FieldEntity> fieldList;

}
