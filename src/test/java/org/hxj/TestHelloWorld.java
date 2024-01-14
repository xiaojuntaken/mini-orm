package org.hxj;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Element;
import org.hxj.entity.common.XmlMethod;
import org.hxj.entity.po.Activity;
import org.hxj.mapper.ActivityMapper;
import org.hxj.mapper.TestMapper;
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
@Slf4j
public class TestHelloWorld {
    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    TestMapper testMapper;
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
        List<Integer> ids = new ArrayList<>();
        List<Activity> list = new ArrayList<>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        Activity activity = new Activity();
        activity.setId(1);
        Activity activity1 = new Activity();
        activity1.setId(2);

        list.add(activity);
        list.add(activity1);
        List<Activity> activities = activityMapper.selectByMiniProgramId(ids,0);
        log.info("result======="+ JSON.toJSONString(activities));
        System.out.println("单元测试");
    }
    @Test
    public void testSelectById(){
        Activity activities = activityMapper.selectById(1);
        activityMapper.deleteById("s");
        System.out.println("单元测试");
    }
    @Test
    public void testUpdateById(){
        org.hxj.entity.po.Test test = testMapper.selectById(1);
        test.setName("测试2");
        testMapper.updateById(test);
        System.out.println("单元测试");
    }


}
