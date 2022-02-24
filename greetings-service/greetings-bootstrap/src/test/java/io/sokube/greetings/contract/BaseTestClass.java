package io.sokube.greetings.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.sokube.greetings.domain.Greeting;
import io.sokube.greetings.domain.GreetingRepository;
import io.sokube.greetings.domain.event.EventEmitter;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, KafkaAutoConfiguration.class})
public class BaseTestClass {

    @MockBean
    private GreetingRepository repository;

    @MockBean
    private EventEmitter eventEmitter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void initialiseRestAssuredMockMvcWebApplicationContext() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails();
        given(repository.find(UUID.fromString("03e805ff-5860-49a6-88bc-a1dcda0dd4b4")))
                .willReturn(Optional.of(Greeting.of("Christmas").to("Marie").build()));
        given(repository.put(any(Greeting.class))).willAnswer(call -> call.getArgument(0));
    }
}
