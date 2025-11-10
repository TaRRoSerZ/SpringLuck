package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.application.domain.service.BetService;
import com.spingluck.SpringLuck.application.domain.service.TransactionService;
import com.spingluck.SpringLuck.application.domain.service.UserService;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.spingluck.SpringLuck.application.port.in.UserUseCase;
import com.spingluck.SpringLuck.application.port.out.BetPort;
import com.spingluck.SpringLuck.application.port.out.TransactionPort;
import com.spingluck.SpringLuck.application.port.out.UserPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringLuckApplicationConfiguration {

    @Autowired
    private BetPort betPort;

    @Autowired
    private TransactionPort transactionPort;

    @Autowired
    private UserPort userPort;

    @Bean
    TransactionUseCase transactionUseCase() {
        return new TransactionService(transactionPort);
    }

    @Bean
    BetUseCase betUseCase() {
        return new BetService(betPort);
    }

    @Bean
    UserUseCase userUseCase() {
        return new UserService(userPort, transactionUseCase());
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
