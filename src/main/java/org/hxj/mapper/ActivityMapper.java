package org.hxj.mapper;

import org.hxj.core.mapper.BaseMapper;
import org.hxj.entity.po.Activity;

import java.util.List;

public interface ActivityMapper extends BaseMapper<Activity> {
    List<Activity> selectByMiniProgramId(List<Integer> ids,int id);
}
