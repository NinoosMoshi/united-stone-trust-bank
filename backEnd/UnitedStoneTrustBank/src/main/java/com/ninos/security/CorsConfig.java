package com.ninos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin("*"); // Allows requests from ANY origin (e.g., http://localhost:3000, https://example.com, etc.)
        config.addAllowedHeader("*"); // // Allows ALL HTTP headers in the request (e.g., Authorization, Content-Type)
        config.addAllowedMethod("*"); // // Allows ALL HTTP methods (e.g., GET, POST, PUT, DELETE, OPTIONS)
        config.setMaxAge(3600L);  // 3600L = 1 hour // // Sets how long (in seconds) the browser can cache the CORS preflight response

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }


}

