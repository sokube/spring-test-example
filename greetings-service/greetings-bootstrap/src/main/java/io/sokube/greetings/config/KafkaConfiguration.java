package io.sokube.greetings.config;

import io.sokube.greetings.message.producer.GreetingsProducerProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration(proxyBeanMethods = false)
public class KafkaConfiguration {
    @Bean
    NewTopic greetingTopic(GreetingsProducerProperties properties) {
        return TopicBuilder.name(
                properties.getTopicName())
                .partitions(properties.getNumPartition())
                .replicas(properties.getReplication())
                .build();
    }
}
