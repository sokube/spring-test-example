package io.sokube.greetings.rest;

import io.sokube.greetings.domain.Greeting;
import io.sokube.greetings.domain.GreetingRepository;
import io.sokube.greetings.domain.event.EventEmitter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication(scanBasePackages = "io.sokube.greetings")
public class GreetingsRestTestApplication {

    @Bean
    GreetingRepository repository() {
        return new GreetingRepository() {
            private final Map<UUID, Greeting> greetings = new HashMap<>();

            @Override
            public Greeting put(Greeting greeting) {
                greetings.put(greeting.getIdentifier(), greeting);
                return greeting;
            }

            @Override
            public Optional<Greeting> find(UUID identifier) {
                return Optional.ofNullable(greetings.get(identifier));
            }
        };
    }

    @Bean
    EventEmitter eventEmitter() {
        return event -> {
        };
    }
}
