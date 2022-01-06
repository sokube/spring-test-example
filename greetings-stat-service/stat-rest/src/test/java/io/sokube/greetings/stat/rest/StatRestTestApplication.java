package io.sokube.greetings.stat.rest;

import io.sokube.greetings.stat.domain.GreetingsStats;
import io.sokube.greetings.stat.domain.StatRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication(proxyBeanMethods = false,
        scanBasePackages = "io.sokube.greetings.stat")
public class StatRestTestApplication {

    @Bean
    StatRepository statRepository() {
        return new StatRepository() {
            private GreetingsStats stats = new GreetingsStats(new HashMap<>());

            @Override
            public void put(GreetingsStats stats) {
                this.stats = stats;
            }

            @Override
            public CompletableFuture<GreetingsStats> pop() {
                return CompletableFuture.completedFuture(stats);
            }
        };
    }
}
