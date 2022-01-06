package io.sokube.greetings.domain;

@FunctionalInterface
public interface MessageCreator {

    String createMessage(String name);
}
