package io.sokube.greetings.message.producer;

public class ProducerException extends RuntimeException {
    public ProducerException(Exception e) {
        super(e);
    }
}
