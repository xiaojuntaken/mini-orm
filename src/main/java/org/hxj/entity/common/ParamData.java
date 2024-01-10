package org.hxj.entity.common;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @authorxiaojun
 * @date2024/1/10 22:50
 */
@Data
@Accessors(chain = true)
public class ParamData {
    private String name;
    private String type;

}
