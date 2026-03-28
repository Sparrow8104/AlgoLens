package com.algolens.algo_lens.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;

@Configuration
public class webClientConfig {
    @Bean
    public WebClient webClient() {
        ExchangeStrategies strategies=ExchangeStrategies.builder()
                .codecs(configurer->
                        configurer.defaultCodecs().maxInMemorySize(20*1024*1024))
                .build();


        return WebClient.builder()
                .baseUrl("https://codeforces.com/api")
                .exchangeStrategies(strategies)
                .build();
    }

}
