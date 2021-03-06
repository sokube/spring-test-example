package io.sokube.greetings.stat.application;

import io.sokube.greetings.stat.domain.GreetingCreated;
import io.sokube.greetings.stat.domain.GreetingsStats;
import io.sokube.greetings.stat.domain.StatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatApplicationService {

    private final StatRepository statRepository;
    private final GreetingsRepository greetingsRepository;

    // TODO make it transactional in a Reactive way...
    public Mono<Void> handle(GreetingCreated event) {
        return Mono.zip(
                greetingsRepository.getGreetingForIdentifier(event.identifier()),
                Mono.fromFuture(statRepository.pop()),
                (greeting, greetingsStats) -> greetingsStats.increaseCounterFor(greeting.type())
        ).flatMap(stats -> Mono.fromRunnable(() -> statRepository.put(stats)));
    }

    public Mono<GreetingsStats> retrieveGreetingsStats() {
        return Mono.fromFuture(statRepository::pop);
    }
}