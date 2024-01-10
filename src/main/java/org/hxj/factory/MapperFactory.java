package org.hxj.factory;

import lombok.Data;
import org.dom4j.Element;
import org.hxj.entity.common.MethodData;
import org.hxj.entity.common.TableMetaData;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.XmlUtils;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author xiaojun
 * @date 2023/12/31 21:11
 */
@Data
public class MapperFactory<T> implements FactoryBean<T> {

    private Class<T> interfaceClass;

    public MapperFactory(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public T getObject() throws SQLException, ClassNotFoundException {
        //初始化代理接口的泛型表的基础数据
        TableMetaData tableMetaData = initTableMetData();

        InvocationHandler invocationHandler = new DynamicMapperProxy<>(interfaceClass, tableMetaData);

        String mapperName = interfaceClass.getSimpleName();
        Map<String, Element> methodMap = XmlUtils.readXml(mapperName);
        if (methodMap != null) {
            MysqlUtils.getXmlMethodMap().put(mapperName, methodMap);
        }
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

    /**
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public TableMetaData initTableMetData() throws SQLException, ClassNotFoundException {
        TableMetaData tableMetaData = new TableMetaData();
        Type[] genericSuperclass = interfaceClass.getGenericInterfaces();
        for (Type type : genericSuperclass) {
            //Type强转成子类ParameterizedType这样就可以通过子类的getActualTypeArguments获取类对象
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (Type a : actualTypeArguments) {
                String enumClassPath = a.getTypeName();
                Class<?> tableClass = Class.forName(enumClassPath);
                //将泛型的类放到被代理的对象里
                tableMetaData.setTableClass(tableClass);
                String[] split = enumClassPath.split("\\.");
                String enumClassName = split[split.length - 1];
                tableMetaData.setTableName(enumClassName.toLowerCase());
            }
        }


        Connection conn = MysqlUtils.getConnection();
        //获取主键名
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet rs = metaData.getPrimaryKeys(null, null, tableMetaData.getTableName());
        while (rs.next()) {
            tableMetaData.setPkColumnName(rs.getString("COLUMN_NAME"));
            break;
        }
        return tableMetaData;
    }


}
