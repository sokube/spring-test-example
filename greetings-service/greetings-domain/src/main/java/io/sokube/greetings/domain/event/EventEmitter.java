package io.sokube.greetings.domain.event;

public interface EventEmitter {
    void emit(GreetingsEvent event);
}
