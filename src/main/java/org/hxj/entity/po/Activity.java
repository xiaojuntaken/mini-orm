package org.hxj.entity.po;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
//@Accessors
public class Activity {
    private Integer id;
    private Integer miniProgramId;
    private String activityName;
    private Date startTime;
}
