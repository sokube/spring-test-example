package io.sokube.greetings.stat.rest.client;

import io.sokube.greetings.stat.application.GreetingsRepository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(GreetingsStatRestClientProperties.class)
public class GreetingsStatRestClientConfiguration {
    @Bean
    GreetingsRepository greetingsRepository(GreetingsStatRestClientProperties properties) {
        return new GreetingsRepositoryAdapter(WebClient.builder().build(), properties);
    }

}
