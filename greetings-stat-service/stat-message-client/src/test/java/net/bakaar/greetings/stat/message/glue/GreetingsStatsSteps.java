package net.bakaar.greetings.stat.message.glue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import net.bakaar.greetings.stat.domain.GreetingCreated;
import net.bakaar.greetings.stat.message.GreetingMessage;
import net.bakaar.greetings.stat.message.TestSpringBootApplication;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@CucumberContextConfiguration
@EmbeddedKafka(partitions = 1)
@SpringBootTest(classes = TestSpringBootApplication.class)
@ActiveProfiles(profiles = "test")
public class GreetingsStatsSteps {

    private final UUID identifier = UUID.randomUUID();
    private final ObjectMapper jsonMapper = JacksonUtils.enhancedObjectMapper();
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;
    @Autowired
    private TestSpringBootApplication.TestApplicationService service;
    @Value("${greetings.message.topic}")
    private String topic;

    @When("I create a greeting")
    public void i_create_a_greetings() throws JsonProcessingException {
        var producerFactory = new DefaultKafkaProducerFactory<String, GreetingMessage>(
                KafkaTestUtils.producerProps(embeddedKafka));
        producerFactory.setKeySerializer(new StringSerializer());
        producerFactory.setValueSerializer(new JsonSerializer<>());
        var producer = producerFactory.createProducer();
        var greetingCreated = new GreetingCreated(identifier, LocalDateTime.now());
        var message = new GreetingMessage(greetingCreated.type(), jsonMapper.writeValueAsString(greetingCreated));
        producer.send(new ProducerRecord<>(topic, identifier.toString(), message));
        producer.flush();
    }

    @Then("the counter should be {int}")
    public void the_counter_should_be(Integer counter) {
        await("Wait For Kafka Message").atMost(Duration.ofSeconds(30)).until(() -> service.getCounter() > 0);
        assertThat(service).isNotNull();
        assertThat(service.getCounter()).isEqualTo(counter);
        assertThat(service.getLastIdentifier()).isEqualTo(identifier);
    }
}
