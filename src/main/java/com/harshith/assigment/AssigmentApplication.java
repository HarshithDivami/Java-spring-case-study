package com.harshith.assigment;

import com.harshith.assigment.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class AssigmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssigmentApplication.class, args);
    }
}
