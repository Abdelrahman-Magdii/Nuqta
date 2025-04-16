package com.spring.nuqta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.spring.nuqta"})
@EnableJpaAuditing
@EnableCaching
@EnableJpaRepositories(basePackages = "com.spring.nuqta")
public class NuqtaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NuqtaApplication.class, args);
    }

}
