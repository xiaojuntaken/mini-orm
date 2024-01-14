package org.hxj.mapper;

import org.hxj.core.mapper.BaseMapper;
import org.hxj.entity.po.Activity;
import org.hxj.entity.po.Test;

import java.util.List;

public interface TestMapper extends BaseMapper<Test> {
        List<Test> selectByMiniProgramId(List<Integer> ids, int id);
//    List selectByMiniProgramId(List<Activity> ids, int id);

}
