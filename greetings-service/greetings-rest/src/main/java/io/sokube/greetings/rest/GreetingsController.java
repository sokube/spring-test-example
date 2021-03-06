package io.sokube.greetings.rest;

import io.sokube.greetings.application.GreetingApplicationService;
import io.sokube.greetings.domain.CreateGreetingCommand;
import io.sokube.greetings.domain.Greeting;
import io.sokube.greetings.domain.UpdateGreetingCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "/rest/api/v1/greetings")
@RequiredArgsConstructor
public class GreetingsController {

    private final GreetingApplicationService applicationService;

    private final GreetingMapper mapper;

    @PostMapping
//            (produces = {APPLICATION_JSON_VALUE}, consumes = {APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GreetingMessage> createGreeting(@RequestBody CreateGreetingCommand command) {
        Greeting createdGreeting = applicationService.createGreeting(command);
        return ResponseEntity
                .created(fromCurrentRequest().path("/{identifier}").buildAndExpand(createdGreeting.getIdentifier()).toUri())
                .body(mapper.mapToMessage(createdGreeting));
    }

    @PutMapping("/{identifier}")
    public ResponseEntity<GreetingMessage> updateGreeting(@PathVariable UUID identifier,
                                                          @RequestBody UpdateGreetingCommandDTO command) {
        return ResponseEntity.ok(mapper.mapToMessage(
                applicationService.changeType(new UpdateGreetingCommand(identifier, command.getNewType()))
        ));
    }

    @GetMapping(value = "/{identifier}", produces = {APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public GreetingJson readGreeting(@PathVariable UUID identifier) {
        return mapper.mapToJson(applicationService.read(identifier));
    }

}
