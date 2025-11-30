package com.relativesHelp.relativesHelp.graphql.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            // Custom scalar types or additional wiring can be added here
            // For now, Spring Boot GraphQL handles most things automatically
        };
    }
}


