package io.sokube.greetings.stat.rest.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.ContextConfiguration;
import reactor.test.StepVerifier;

import java.util.UUID;

@WebFluxTest
@ContextConfiguration(classes = GreetingsStatRestClientConfiguration.class)
@AutoConfigureStubRunner(ids = {"io.sokube:greetings-bootstrap:+:stubs:6565"},
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class GreetingRepositoryAdapterIT {

    private final String stringIdentifier = "03e805ff-5860-49a6-88bc-a1dcda0dd4b4";
    private final UUID identifier = UUID.fromString(stringIdentifier);
    @Autowired
    private GreetingsStatRestClientProperties properties;
    @Autowired
    private GreetingsRepositoryAdapter client;

    @Test
    void should_read_greetings_from_pact() {
        properties.setUrl("http://localhost:6565/rest/api/v1/greetings");
        StepVerifier
                .create(client.getGreetingForIdentifier(identifier))
                .expectNextCount(1)
                .verifyComplete();
    }
}
