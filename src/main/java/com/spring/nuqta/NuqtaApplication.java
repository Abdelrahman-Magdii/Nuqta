package com.spring.nuqta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.spring.nuqta"})
@EnableJpaAuditing
@EnableCaching
public class NuqtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuqtaApplication.class, args);
    }

}
