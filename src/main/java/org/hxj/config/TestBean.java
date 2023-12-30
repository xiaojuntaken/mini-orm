package org.hxj.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class TestBean {

    private Long id;

    public TestBean() {
        System.out.println("test bean init.....");
    }

}
