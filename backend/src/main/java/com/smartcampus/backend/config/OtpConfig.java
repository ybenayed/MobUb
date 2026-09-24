package com.smartcampus.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// RestTemplate dedie aux appels vers OTP (calcul d'itineraire).

@Configuration
public class OtpConfig {

    @Bean(name = "otpRestTemplate")
    public RestTemplate otpRestTemplate() {
        return new RestTemplate();
    }
}