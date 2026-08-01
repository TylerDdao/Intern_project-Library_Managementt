package com.example.library_management.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File dir = new File(System.getProperty("user.dir"), uploadDir);
        String absolutePath = dir.getAbsolutePath() + "/";
        System.out.println("Serving from: " + absolutePath);
        registry.addResourceHandler("/book-covers/**")
                .addResourceLocations("file:" + absolutePath);
    }
}