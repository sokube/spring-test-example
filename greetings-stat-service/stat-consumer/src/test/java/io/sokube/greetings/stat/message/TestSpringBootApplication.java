package io.sokube.greetings.stat.message;

import io.sokube.greetings.stat.application.GreetingsRepository;
import io.sokube.greetings.stat.application.readmodel.Greeting;
import io.sokube.greetings.stat.domain.GreetingsStats;
import io.sokube.greetings.stat.domain.StatRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication(proxyBeanMethods = false, scanBasePackages = "io.sokube.greetings.stat")
public class TestSpringBootApplication {

    @Bean
    NewTopic greetingTopic(GreetingsMessageProperties properties) {
        return new NewTopic(properties.getTopic(), 1, (short) 1);
    }

    // MockBean doesn't work with Cucumber
    @Bean
    GreetingsRepository greetingsRepository() {
        return new TestGreetingsRepository();
    }

    // MockBean doesn't work with Cucumber
    @Bean
    StatRepository statRepository() {
        return new StatRepository() {
            private GreetingsStats stats = new GreetingsStats(new HashMap<>(Map.of("BIRTHDAY", 0L, "ANNIVERSARY", 0L, "CHRISTMAS", 0L)));

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


    public static class TestGreetingsRepository implements GreetingsRepository {

        private final Map<UUID, Greeting> greetings = new HashMap<>();

        public void addGreeting(UUID identifier, Greeting greeting) {
            greetings.put(identifier, greeting);
        }

        @Override
        public Mono<Greeting> getGreetingForIdentifier(UUID identifier) {
            return Mono.just(greetings.get(identifier));
        }
    }
}
