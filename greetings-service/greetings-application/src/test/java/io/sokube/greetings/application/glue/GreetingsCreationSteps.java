package io.sokube.greetings.application.glue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.sokube.greetings.application.GreetingApplicationService;
import io.sokube.greetings.domain.CreateGreetingCommand;
import io.sokube.greetings.domain.Greeting;
import io.sokube.greetings.domain.UpdateGreetingCommand;
import io.sokube.greetings.domain.event.GreetingCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@CucumberContextConfiguration
@ContextConfiguration(classes = GreetingApplicationTestConfiguration.class)
public class GreetingsCreationSteps {

    @Autowired
    private GreetingApplicationService service;

    @Autowired
    private StubbedEventEmitter emitter;

    private Greeting createdGreeting;
    private Greeting updatedGreeting;

    private Throwable thrown;

    @Given("an existing {word} greeting")
    public void an_existing_greeting(String type) {
        createdGreeting = service.createGreeting(new CreateGreetingCommand(type, "Charlotte"));
    }

    @When("I create a(n) {word} greeting for {word}")
    public void iCreateAGreetingForName(String type, String name) {
        thrown = catchThrowable(() -> createdGreeting = service.createGreeting(new CreateGreetingCommand(type, name)));
    }

    @When("I change the type to {word}")
    public void i_change_the_type_to(String type) {
        thrown = catchThrowable(() -> updatedGreeting = service.changeType(new UpdateGreetingCommand(createdGreeting.getIdentifier(), type)));
    }

    @Then("I get the message {string}")
    public void iGetTheMessage(String message) {
        assertThat(createdGreeting.getMessage()).isEqualTo(message);
    }

    @Then("a Greeting is created")
    public void a_greeting_is_created() {
        assertThat(emitter.getEmittedEvent()).isInstanceOf(GreetingCreated.class);
    }

    @Then("I get an error")
    public void iGetAnError() {
        assertThat(thrown).isNotNull().isInstanceOf(IllegalArgumentException.class);
    }

    @Then("the greeting is now a {word} one")
    public void the_greeting_is_now_a_new_type_one(String type) {
        assertThat(updatedGreeting.getType()).hasToString(type.toUpperCase(Locale.ROOT));
    }
}
