package com.epam.gcp.csv.processor.pubsub.message.publisher;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
public interface PubSubMessagePublisher {

  void publish(String value);
}
