package io.sokube.greetings.stat.rest;

import io.sokube.greetings.stat.application.StatApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(path = "/rest/api/v1/stats")
@RequiredArgsConstructor
public class StatRestController {

    private final StatApplicationService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GreetingsStatsJson> getAllStats() {
        return service.retrieveGreetingsStats()
                .log(log.getName())
                .map(stats -> new GreetingsStatsJson(stats.getCounters()));
    }
}
