package org.hxj.annotation;

import lombok.extern.slf4j.Slf4j;
import org.hxj.config.MysqlConfig;
import org.hxj.entity.po.Activity;
import org.hxj.factory.MapperFactory;
import org.hxj.mapper.ActivityMapper;
import org.hxj.table.TableMetaData;
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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        MysqlUtils.connectionMysql();
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
                    BeanDefinitionBuilder beanDefinitionBuilder = initBeanDefinitionBuilder(clazz);
                    registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinitionBuilder.getBeanDefinition());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private BeanDefinitionBuilder initBeanDefinitionBuilder(Class<?> clazz) {
        //创建一个通用的beanDefinition，填充bean的class为接口的类
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        try {
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanDefinitionBuilder.getRawBeanDefinition();
            beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(clazz);
            beanDefinition.setBeanClass(MapperFactory.class);
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return beanDefinitionBuilder;
    }
}
