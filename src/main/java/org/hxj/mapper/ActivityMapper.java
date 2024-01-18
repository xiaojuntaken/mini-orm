package org.hxj.mapper;

import org.hxj.core.mapper.BaseMapper;
import org.hxj.entity.po.Activity;
import org.hxj.entity.vo.TestVo;

import java.util.List;

public interface ActivityMapper extends BaseMapper<Activity> {
        List<Activity> selectByMiniProgramId(List<Integer> ids,int id);

        List<Activity> selectByMiniProgramIds(TestVo param, int id);

//    List selectByMiniProgramId(List<Activity> ids, int id);

}
