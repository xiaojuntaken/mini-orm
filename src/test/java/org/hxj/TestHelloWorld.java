package org.hxj;

import org.hxj.entity.common.XmlMethod;
import org.hxj.mapper.ActivityMapper;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.XmlUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestHelloWorld {
    @Autowired
    ActivityMapper activityMapper;
    @Test
    public void test(){
        String mapperName = "ActivityMapper.xml";
        List<XmlMethod> objects = XmlUtils.readXml(mapperName);
        MysqlUtils.getXmlMethodMap().put(mapperName,objects);
        System.out.println("单元测试");
    }
}
