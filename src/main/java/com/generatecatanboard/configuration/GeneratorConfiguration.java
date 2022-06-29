package com.generatecatanboard.configuration;

import com.contentful.java.cda.CDAClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GeneratorConfiguration {

    @Value("${contentful.space}")
    private String contentfulSpace;
    @Value("${contentful.accessToken}")
    private String contentfulAccessToken;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**");
            }
        };
    }

    @Bean
    public CDAClient cdaClient() {
        return CDAClient.builder()
                .setSpace(contentfulSpace)
                .setToken(contentfulAccessToken)
                .build();
    }
}
