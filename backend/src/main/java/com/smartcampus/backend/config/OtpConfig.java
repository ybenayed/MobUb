package com.smartcampus.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate dedie aux appels vers OTP (calcul d'itineraire).
 * Separe du RestTemplate generique (AppConfig) pour pouvoir plus tard lui
 * ajouter des timeouts/interceptors specifiques sans impacter le reste,
 * meme principe que NavitiaConfig.
 */
@Configuration
public class OtpConfig {

    @Bean(name = "otpRestTemplate")
    public RestTemplate otpRestTemplate() {
        return new RestTemplate();
    }
}