package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.application.domain.service.BetService;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
import com.spingluck.SpringLuck.application.port.out.BetPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringLuckApplicationConfiguration {

    @Autowired
    private BetPort betPort;

    @Bean
    BetUseCase betUseCase() {
        return new BetService(betPort);
    }

}
