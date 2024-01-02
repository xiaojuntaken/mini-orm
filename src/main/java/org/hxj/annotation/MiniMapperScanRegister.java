package org.hxj.annotation;

import lombok.extern.slf4j.Slf4j;
import org.hxj.config.MysqlConfig;
import org.hxj.entity.po.Activity;
import org.hxj.factory.MapperFactory;
import org.hxj.mapper.ActivityMapper;
import org.hxj.utils.ClassLoadUtils;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.StatementUtils;
import org.hxj.utils.StringUtils;
import org.springframework.beans.factory.support.*;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ImportBeanDefinitionRegister接口是spring扩展点，可以支持自己写的代码封装成BeanDefinition对象实现此接口的类会回调
 * postProcessBeanDefinitionRegistry方法，注册到spring容器中。
 */
@Slf4j
public class MiniMapperScanRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    public static Map<String, Class<?>> poMap = new HashMap<>();

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }


    /**
     * 通过注解HxjMapperScan的值，获取所有的mapper接口
     *
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {



        //获取有@import注解的值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(MiniMapperScan.class.getName());
        assert annotationAttributes != null;
        Set<String> keySet = annotationAttributes.keySet();
        for (String key : keySet) {
            String packageName = (String) annotationAttributes.get(key);
            try {
                //根据包名获取类集合
                List<Class<?>> clazzList = ClassLoadUtils.getClassesByPackageName(packageName);
                for (Class<?> clazz : clazzList) {
                    //将包下的接口注册生成BeanDefinition
                    //创建一个通用的beanDefinition，填充bean的class为接口的类
                    BeanDefinitionBuilder beanDefinitionBuilder= BeanDefinitionBuilder.genericBeanDefinition(clazz);
                    GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
                    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(clazz);
                    beanDefinition.setBeanClass(MapperFactory.class);
                    beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                    registry.registerBeanDefinition(clazz.getSimpleName(),beanDefinition);

//                    //获取继承的接口
//                    Type[] genericInterfaces = clazz.getGenericInterfaces();
//                    for (Type interfaces : genericInterfaces) {
//                        ParameterizedType parameterizedType = (ParameterizedType) interfaces;
//                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//                        for (Type type : actualTypeArguments) {
//                            //接口继承接口的泛型-实体类泛型
//                            String modelClass = type.getTypeName();
//                            Class<?> modelClazz = Class.forName(modelClass);
//                            poMap.put(modelClazz.getSimpleName(), modelClazz);
//                            String resultSql = MysqlUtils.packageResultSqlByClass(modelClazz);
//                            String resultSql1 = MysqlUtils.packageResultSqlByClass(poMap.get(modelClazz.getSimpleName()));
//                            String tableName = StringUtils.toUnderScoreCase(modelClazz.getSimpleName());
//                            String sql = MysqlUtils.packBaseSql(resultSql, tableName, "");
//                            Connection connection = MysqlConfig.connectionMysql();
//                            StatementUtils.querySql(connection, sql);
//                        }
//                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void test(){
        //创建beanfactory构造器
//        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ActivityMapper.class);
//        //创建beanDefinition
//        GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
//        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(ActivityMapper.class);
//        //将bean的真实类型改变为FactoryBea
//        beanDefinition.setBeanClass(MapperFactory.class);
//        beanDefinition.getPropertyValues().add("interfaceClass", ActivityMapper.class);
//        beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
//        registry.registerBeanDefinition(ActivityMapper.class.getSimpleName(), beanDefinition);
    }

}
