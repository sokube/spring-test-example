package io.sokube.greetings.domain.event;

import io.sokube.greetings.domain.Greeting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GreetingCreated extends GreetingsEvent {
    private final UUID identifier;

    public static GreetingCreated of(Greeting greeting) {
        return new GreetingCreated(greeting.getIdentifier());
    }
}
