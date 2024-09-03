package com.bugracdnc.restmvc;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Data
@SpringBootApplication
public class RestMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestMvcApplication.class, args);
    }
}
