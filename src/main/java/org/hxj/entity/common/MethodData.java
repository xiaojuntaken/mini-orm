package org.hxj.entity.common;

import lombok.Data;

import java.util.List;

/**
 * @author xiaojun
 * @date 2024/1/10 22:48
 */
@Data
public class MethodData {
    private String name;
    private List<ParamData> params;
}
