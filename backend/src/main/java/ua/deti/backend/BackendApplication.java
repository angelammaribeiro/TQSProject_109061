package ua.deti.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "ua.deti.backend",
    "ua.deti.backend.controller",
    "ua.deti.backend.service",
    "ua.deti.backend.service.impl",
    "ua.deti.backend.repository"
})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
