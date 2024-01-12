package org.hxj;

import org.dom4j.Element;
import org.hxj.entity.common.XmlMethod;
import org.hxj.entity.po.Activity;
import org.hxj.mapper.ActivityMapper;
import org.hxj.utils.MysqlUtils;
import org.hxj.utils.XmlUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//加上@RunWith(SpringRunner.class)运行单元测试类的时候，启动整个项目达到依赖注入的项目
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class)
public class TestHelloWorld {
    @Autowired
    ActivityMapper activityMapper;
    @Test
    public void testReadXml(){
        String mapperName = "ActivityMapper.xml";
        Map<String, Element> stringElementMap = XmlUtils.readXml(mapperName);
        System.out.println("单元测试");
    }

    /**
     * 测试执行xml的方法
     */
    @Test
    public void testExecuteXmlMethod(){
//        List<Activity> ids = new ArrayList<>();
        List<Activity> list = new ArrayList<>();
//        ids.add(1);
//        ids.add(2);
//        ids.add(3);
        Activity activity = new Activity();
        activity.setId(1);
        list.add(activity);
        list.add(activity);
        List<Activity> activities = activityMapper.selectByMiniProgramId(list,0);

        System.out.println("单元测试");
    }
}
