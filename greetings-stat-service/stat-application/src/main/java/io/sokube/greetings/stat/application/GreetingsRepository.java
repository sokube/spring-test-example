package io.sokube.greetings.stat.application;

import io.sokube.greetings.stat.application.readmodel.Greeting;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GreetingsRepository {

    Mono<Greeting> getGreetingForIdentifier(UUID identifier);
}
