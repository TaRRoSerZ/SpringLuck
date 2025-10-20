package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.application.domain.service.BetService;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
import com.spingluck.SpringLuck.application.port.out.BetPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringLuckApplicationConfiguration {

    @Autowired
    private BetPort betPort;

    @Bean
    BetUseCase betUseCase() {
        return new BetService(betPort);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // toutes les routes
                        .allowedOrigins("http://localhost:5173") // ton front
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

}
