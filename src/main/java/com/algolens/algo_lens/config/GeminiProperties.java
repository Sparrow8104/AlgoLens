package com.algolens.algo_lens.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@ConfigurationProperties(prefix = "gemini.api")
@Getter
@Setter
public class GeminiProperties {
    private String key;
    private String url;
}
