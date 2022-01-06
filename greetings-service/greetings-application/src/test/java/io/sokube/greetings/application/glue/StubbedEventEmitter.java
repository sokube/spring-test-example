package io.sokube.greetings.application.glue;

import io.sokube.greetings.domain.event.EventEmitter;
import io.sokube.greetings.domain.event.GreetingsEvent;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class StubbedEventEmitter implements EventEmitter {

    private GreetingsEvent emittedEvent;

    @Override
    public void emit(GreetingsEvent event) {
        this.emittedEvent = event;
    }
}
