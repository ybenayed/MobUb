package com.smartcampus.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
// token pour l'API navitia, a mettre dans le fichier application.properties
@Configuration
public class NavitiaConfig {

    @Value("${navitia.token}")
    private String navitiaToken;

    @Bean(name = "navitiaRestTemplate")
    public RestTemplate navitiaRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        restTemplate.getInterceptors().add(
            new BasicAuthenticationInterceptor(navitiaToken, "")
        );
        
        return restTemplate;
    }
}