package io.sokube.greetings.domain;

import java.util.UUID;

public record UpdateGreetingCommand(UUID identifier, String newType) {
}
