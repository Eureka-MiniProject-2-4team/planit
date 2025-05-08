package com.eureka.mp2.team4.planit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.eureka.mp2.team4.planit")
public class PlanitApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanitApplication.class, args);
    }

}
