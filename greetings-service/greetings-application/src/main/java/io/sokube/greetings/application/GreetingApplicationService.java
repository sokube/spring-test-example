package io.sokube.greetings.application;

import io.sokube.greetings.application.exception.GreetingNotFoundException;
import io.sokube.greetings.domain.CreateGreetingCommand;
import io.sokube.greetings.domain.Greeting;
import io.sokube.greetings.domain.GreetingRepository;
import io.sokube.greetings.domain.UpdateGreetingCommand;
import io.sokube.greetings.domain.event.EventEmitter;
import io.sokube.greetings.domain.event.GreetingCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class GreetingApplicationService {
    private final GreetingRepository repository;
    private final EventEmitter emitter;

    @Transactional
    public Greeting createGreeting(CreateGreetingCommand command) {
        var greeting = Greeting.of(command.type()).to(command.name()).build();
        emitter.emit(GreetingCreated.of(greeting));
        return repository.put(greeting);
    }

    @Transactional
    public Greeting changeType(UpdateGreetingCommand command) {
        Greeting greeting = read(command.identifier());
        greeting.updateTypeFor(command.newType());
        return repository.put(greeting);
    }

    @Transactional(readOnly = true)
    public Greeting read(UUID identifier) {
        return repository.find(identifier)
                .orElseThrow(() ->
                        new GreetingNotFoundException(format("Greeting with identifier %s not found", identifier))
                );
    }
}
