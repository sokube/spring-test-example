package io.sokube.greetings.stat.message.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.sokube.greetings.stat.application.StatApplicationService;
import io.sokube.greetings.stat.domain.GreetingCreated;
import io.sokube.greetings.stat.message.exception.JsonDeserializationException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor
public class CreatedGreetingEventPayloadHandler implements GreetingMessagePayloadHandler {

    private static final URI TYPE = URI.create("https://sokube.io/greetings/events/greeting-created");
    private final StatApplicationService service;
    private final ObjectMapper jsonMapper;

    @Override
    public boolean canHandle(URI type) {
        return TYPE.equals(type);
    }

    @Override
    public Mono<Void> handle(String payload) {
        return Mono.fromCallable(() -> jsonMapper.readValue(payload, GreetingCreated.class))
                .onErrorMap(JsonDeserializationException::new)
                .flatMap(service::handle);

    }
}
