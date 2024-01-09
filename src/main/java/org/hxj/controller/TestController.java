package org.hxj.controller;

import com.alibaba.fastjson.JSON;
import org.hxj.entity.po.Activity;
import org.hxj.entity.po.Banner;
import org.hxj.mapper.ActivityMapper;
import org.hxj.mapper.BannerMapper;
import org.hxj.utils.XmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @authorxiaojun
 * @date2023/12/31 14:39
 */
@RestController
public class TestController {
    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    BannerMapper bannerMapper;

    @RequestMapping("/test")
    public String test() {
        XmlUtils.readXml("ActivityMapper.xml");
        Activity activity = new Activity();
        activity.setId(1);
        activity.setMiniProgramId(40);
        List<Activity> list = activityMapper.selectByParam(activity);
        return JSON.toJSONString(list);
    }
}
