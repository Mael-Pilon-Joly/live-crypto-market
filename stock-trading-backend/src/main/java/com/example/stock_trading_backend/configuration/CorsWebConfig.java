package com.example.stock_trading_backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsWebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Call the method to configure CORS
        configureCors(registry, allowedOrigin);
    }

    @Value("${spring.mvc.cors.allowed-origins}")
    private String allowedOrigin;

    protected void configureCors(CorsRegistry registry, String allowedOrigin) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders("header1", "header2")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
