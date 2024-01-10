package org.hxj.entity.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class XmlMethod {
    private String type;
    private String methodName;
    private String sql;
    private List<String> params;
}
