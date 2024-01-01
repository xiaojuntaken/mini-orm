package org.hxj.factory;

import org.hxj.entity.po.Activity;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @authorxiaojun
 * @date2024/1/1 22:46
 */
@Component
public class ActivityFactory implements FactoryBean<Activity> {
    @Override
    public Activity getObject() throws Exception {
        return new Activity();
    }

    @Override
    public Class<?> getObjectType() {
        return Activity.class;
    }
}
