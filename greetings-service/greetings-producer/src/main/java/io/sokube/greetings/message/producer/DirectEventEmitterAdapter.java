package io.sokube.greetings.message.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sokube.greetings.domain.event.EventEmitter;
import io.sokube.greetings.domain.event.GreetingsEvent;
import io.sokube.greetings.message.GreetingsMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import java.net.URI;

@RequiredArgsConstructor
public class DirectEventEmitterAdapter implements EventEmitter {
    private final GreetingsProducerProperties properties;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String, GreetingsMessage> template;

    @Override
    public void emit(GreetingsEvent event) {
        try {
            template.send(properties.getTopicName(),
                    new GreetingsMessage(
                            URI.create("https://sokube.io/greetings/events/greeting-created"),
                            mapper.writeValueAsString(event))
            );
        } catch (JsonProcessingException e) {
            throw new ProducerException(e);
        }
    }
}
