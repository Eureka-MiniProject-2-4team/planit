package com.eureka.mp2.team4.planit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PlanitApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanitApplication.class, args);
    }

}
