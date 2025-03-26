package com.example.Board.config;

import com.querydsl.core.annotations.Config;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;

@Config
public class SwaggerConfig
{
    @Bean
    public GroupedOpenApi restApi(){

        return GroupedOpenApi.builder()
                .pathsToMatch("/**")
                .group("REST API")
                .build();
    }

    @Bean
    public GroupedOpenApi commonApi() {

        return GroupedOpenApi.builder()
                .pathsToMatch("/**/*")
                .pathsToExclude("/api/**/*")
                .group("COMMON API")
                .build();
    }
}
