package org.hxj.controller;

import com.alibaba.fastjson.JSON;
import org.hxj.entity.po.Activity;
import org.hxj.mapper.ActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @authorxiaojun
 * @date2023/12/31 14:39
 */
@RestController
public class TestController {
    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    Activity activity;
    @RequestMapping("/test")
    public String test() {
        Activity activity = activityMapper.selectById(1);
        return JSON.toJSONString(activity);
    }
}
