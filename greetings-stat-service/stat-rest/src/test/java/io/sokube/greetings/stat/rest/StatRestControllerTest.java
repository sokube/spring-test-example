package io.sokube.greetings.stat.rest;

import io.sokube.greetings.stat.application.StatApplicationService;
import io.sokube.greetings.stat.domain.GreetingsStats;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatRestControllerTest {

    @Mock
    private StatApplicationService service;

    @InjectMocks
    private StatRestController controller;

    @Test
    void should_call_application_service() {
        // Given
        var stats = mock(GreetingsStats.class);
        var counters = mock(Map.class);
        given(stats.getCounters()).willReturn(counters);
        given(service.retrieveGreetingsStats()).willReturn(Mono.just(stats));
        // When
        StepVerifier.create(controller.getAllStats())
                .expectNextMatches(json -> json.counters().equals(counters))
                .verifyComplete();
        // Then
        verify(service).retrieveGreetingsStats();
    }
}