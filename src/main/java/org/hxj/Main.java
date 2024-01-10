package org.hxj;

import lombok.extern.slf4j.Slf4j;
import org.hxj.annotation.MiniMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@MiniMapperScan("org.hxj.mapper")
@SpringBootApplication
public class Main {
    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }
}