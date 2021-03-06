package io.sokube.greetings.stat.message.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sokube.greetings.stat.application.StatApplicationService;
import io.sokube.greetings.stat.domain.GreetingCreated;
import io.sokube.greetings.stat.message.exception.JsonDeserializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreatedGreetingEventPayloadHandlerTest {

    @Mock
    private StatApplicationService service;

    @Mock
    private ObjectMapper jsonMapper;

    @InjectMocks
    private CreatedGreetingEventPayloadHandler handler;

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "https://google.com",
            "https://sokube.io/greetings/events/greeting-createds",
            "http://sokube.io/greetings/events/greeting-created/1",
            "http://sokube.io/greetings/events/greeting-created"
    })
    void canHandle_should_return_false(String type) {
        // Given
        // When
        assertThat(handler.canHandle(URI.create(type))).isFalse();
        // Then
    }

    @Test
    void canHandle_should_return_true() {
        assertThat(handler.canHandle(URI.create("https://sokube.io/greetings/events/greeting-created"))).isTrue();
    }

    @Test
    void handle_should_call_jsonMapper_and_service() throws JsonProcessingException {
        // Given
        var payload = "Payload";
        var event = mock(GreetingCreated.class);
        given(jsonMapper.readValue(payload, GreetingCreated.class)).willReturn(event);
        given(service.handle(any())).willReturn(Mono.empty());
        // When
        StepVerifier.create(handler.handle(payload))
                .expectComplete()
                .verify();
        // Then
        verify(jsonMapper).readValue(payload, GreetingCreated.class);
        verify(service).handle(event);
    }

    @Test
    void handle_should_throw_exception() throws JsonProcessingException {
        // Given
        var cause = new JsonEOFException(null, null, null);
        given(jsonMapper.readValue(anyString(), any(Class.class))).willThrow(cause);
        // When
        StepVerifier.create(handler.handle("Whatever"))
                .expectErrorMatches(throwable -> throwable instanceof JsonDeserializationException &&
                        throwable.getCause().equals(cause))
                .verify();
    }
}