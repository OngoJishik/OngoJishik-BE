package com.project.ongojisik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class OngoJisikApplication {

    public static void main(String[] args) {
        SpringApplication.run(OngoJisikApplication.class, args);
    }

}
