package io.sokube.greetings.stat.message.glue;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.sokube.greetings.message.GreetingsMessage;
import io.sokube.greetings.stat.application.readmodel.Greeting;
import io.sokube.greetings.stat.domain.StatRepository;
import io.sokube.greetings.stat.message.TestSpringBootApplication;
import io.sokube.greetings.stat.message.TestSpringBootApplication.TestGreetingsRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@CucumberContextConfiguration
@EmbeddedKafka(partitions = 1)
@SpringBootTest(classes = TestSpringBootApplication.class)
public class GreetingsStatsSteps {

    private final UUID identifier = UUID.randomUUID();
    private final String type = "birthday";
    private final String name = "Albert";
    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;
    @Autowired
    private StatRepository statRepository;
    @Autowired
    private TestGreetingsRepository greetingsRepository;
    @Value("${greetings.message.topic}")
    private static final String topic = "stat_topic";

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("greetings.message.topic", () -> topic);
        registry.add("spring.kafka.bootstrap-servers", () -> "${spring.embedded.kafka.brokers}");
        registry.add("greetings.stat.rest.client.url", () -> "http://localhost:${wiremock.server.port}/rest/api/v1/greetings");
    }

    @When("I create a greeting")
    public void i_create_a_greetings() {
        // send the event on Kafka
        var producerFactory = new DefaultKafkaProducerFactory<String, GreetingsMessage>(
                KafkaTestUtils.producerProps(embeddedKafka));
        producerFactory.setKeySerializer(new StringSerializer());
        producerFactory.setValueSerializer(new JsonSerializer<>());
        var producer = producerFactory.createProducer();
        var message = new GreetingsMessage(URI.create("https://sokube.io/greetings/events/greeting-created"), """
                {
                   "identifier": "%s",
                   "raisedAt" : "2010-01-01T12:00:00+01:00"
                }
                """.formatted(identifier));
        producer.send(new ProducerRecord<>(topic, identifier.toString(), message));
        producer.flush();
        // Mock the call to greeting service
        var greeting = new Greeting(type, name);
        greetingsRepository.addGreeting(identifier, greeting);
    }

    @Then("the counter should be {long}")
    public void the_counter_should_be(Long counter) throws ExecutionException, InterruptedException {
        await().until(() -> statRepository.pop().get().getStatsFor(type).get().equals(counter));
        var stats = statRepository.pop();
        assertThat(stats).isNotNull();
        assertThat(stats.get().getStatsFor(type)).isPresent().get().isEqualTo(counter);
    }
}
