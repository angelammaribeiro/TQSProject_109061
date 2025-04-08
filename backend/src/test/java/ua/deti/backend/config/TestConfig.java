package ua.deti.backend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
@EnableWebMvc
@ComponentScan(basePackages = "ua.deti.backend")
public class TestConfig implements WebMvcConfigurer {
} 