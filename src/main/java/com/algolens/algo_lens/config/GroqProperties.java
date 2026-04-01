package com.algolens.algo_lens.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@ConfigurationProperties(prefix = "groq.api")
@Getter
@Setter
public class GroqProperties {
    private String key;
    private String url;
}
