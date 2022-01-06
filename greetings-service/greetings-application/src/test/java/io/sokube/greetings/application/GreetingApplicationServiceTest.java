package io.sokube.greetings.application;

import io.sokube.greetings.application.exception.GreetingNotFoundException;
import io.sokube.greetings.domain.CreateGreetingCommand;
import io.sokube.greetings.domain.Greeting;
import io.sokube.greetings.domain.GreetingRepository;
import io.sokube.greetings.domain.UpdateGreetingCommand;
import io.sokube.greetings.domain.event.EventEmitter;
import io.sokube.greetings.domain.event.GreetingCreated;
import io.sokube.greetings.domain.event.GreetingsEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GreetingApplicationServiceTest {

    @Mock
    private GreetingRepository repository;
    @Mock
    private EventEmitter emitter;
    @InjectMocks
    private GreetingApplicationService service;

    @Test
    void create_should_call_repository_and_emit_event() {
        // Given
        given(repository.put(any())).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        // When
        var command = new CreateGreetingCommand("christmas", "Vivianne");
        var returnedGreeting = service.createGreeting(command);
        // Then
        var greetingCaptor = ArgumentCaptor.forClass(Greeting.class);
        verify(repository).put(greetingCaptor.capture());
        var eventCaptor = ArgumentCaptor.forClass(GreetingsEvent.class);
        verify(emitter).emit(eventCaptor.capture());
        var savedGreeting = greetingCaptor.getValue();
        assertThat(returnedGreeting).isSameAs(savedGreeting);
        var expectedEvent = eventCaptor.getValue();
        assertThat(expectedEvent).isInstanceOf(GreetingCreated.class);
        assertThat(((GreetingCreated) expectedEvent).getIdentifier()).isEqualTo(returnedGreeting.getIdentifier());
    }

    @Test
    void changeType_should_read_repo() {
        // Given
        var greeting = mock(Greeting.class);
        var identifier = UUID.randomUUID();
        given(repository.find(identifier)).willReturn(Optional.of(greeting));
        given(repository.put(any())).willAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
        var type = "type";
        var command = new UpdateGreetingCommand(identifier, type);
        // When
        var returnedGreeting = service.changeType(command);
        // Then
        assertThat(returnedGreeting).isSameAs(greeting);
        verify(greeting).updateTypeFor(type);
    }

    @Test
    void changeType_should_send_exception_if_not_found() {
        // Given
        var identifier = UUID.randomUUID();
        given(repository.find(identifier)).willReturn(Optional.empty());
        var command = new UpdateGreetingCommand(identifier, "birthday");
        // When
        var thrown = catchThrowable(() -> service.changeType(command));
        // Then
        assertThat(thrown).isNotNull().isInstanceOf(GreetingNotFoundException.class);
    }

    @Test
    void read_should_throw_exception_if_identifier_not_found() {
        // Given
        var identifier = UUID.randomUUID();
        given(repository.find(any())).willReturn(Optional.empty());
        // When
        Throwable thrown = catchThrowable(() -> service.read(identifier));
        // Then
        assertThat(thrown).isInstanceOf(GreetingNotFoundException.class);
        assertThat(thrown.getMessage()).contains(identifier.toString());
    }

    @Test
    void read_should_call_the_repository() {
        // Given
        var identifier = UUID.randomUUID();
        var greeting = mock(Greeting.class);
        given(repository.find(any())).willReturn(Optional.of(greeting));
        // When
        var returnedGreeting = service.read(identifier);
        // Then
        verify(repository).find(identifier);
        assertThat(returnedGreeting).isSameAs(greeting);
    }
}