package io.sokube.greetings.local;

import io.sokube.greetings.GreetingsApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

// Run compose-local.yaml before run this
public class LocalStart {
    public static void main(String[] args) {
        new SpringApplicationBuilder(GreetingsApplication.class).profiles("local")
                .run();
    }
}
