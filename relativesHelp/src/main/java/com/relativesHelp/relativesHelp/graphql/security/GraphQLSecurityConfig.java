package com.relativesHelp.relativesHelp.graphql.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.SecurityContextThreadLocalAccessor;

@Configuration
public class GraphQLSecurityConfig {

    @Bean
    public SecurityContextThreadLocalAccessor securityContextThreadLocalAccessor() {
        return new SecurityContextThreadLocalAccessor();
    }
}


