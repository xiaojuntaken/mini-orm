package org.hxj.annotation;

import lombok.extern.slf4j.Slf4j;
import org.hxj.config.MysqlConfig;
import org.hxj.utils.ClassLoadUtils;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.StatementUtils;
import org.hxj.utils.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ImportBeanDefinitionRegister接口是spring扩展点，可以支持自己写的代码封装成BeanDefinition对象实现此接口的类会回调
 * postProcessBeanDefinitionRegistry方法，注册到spring容器中。
 */
@Slf4j
public class HxjMapperScanRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<String> annotationTypes = importingClassMetadata.getAnnotationTypes();
        //获取有@import注解的值
        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(HxjMapperScan.class.getName());
        assert annotationAttributes != null;
        Set<String> keySet = annotationAttributes.keySet();
        for (String key : keySet) {
            String packageName = (String) annotationAttributes.get(key);
            try {
                //根据包名获取类集合
                List<Class<?>> clazzList = ClassLoadUtils.getClassesByPackageName(packageName);
                for (Class<?> clazz : clazzList) {
                    //获取继承的接口
                    Type[] genericInterfaces = clazz.getGenericInterfaces();
                    for (Type interfaces : genericInterfaces) {
                        ParameterizedType parameterizedType = (ParameterizedType) interfaces;
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        for (Type type : actualTypeArguments) {
                            //接口继承接口的泛型-实体类泛型
                            String modelClass = type.getTypeName();
                            Class<?> modelClazz = Class.forName(modelClass);
                            String resultSql = MysqlUtils.packageResultSqlByClass(modelClazz);
                            String tableName = StringUtils.toUnderScoreCase(modelClazz.getSimpleName());
                            String sql = MysqlUtils.packBaseSql(resultSql, tableName, "");
                            Connection connection = MysqlConfig.connectionMysql();
                            StatementUtils.querySql(connection,sql);
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Set<MethodMetadata> annotatedMethods = importingClassMetadata.getDeclaredMethods();
        System.out.println("=============registerBeanDefinitions============");
    }

}
