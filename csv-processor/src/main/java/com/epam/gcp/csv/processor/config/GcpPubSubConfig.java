package com.epam.gcp.csv.processor.config;

import com.epam.gcp.csv.processor.pubsub.message.publisher.PubSubMessagePublisher;
import com.epam.gcp.csv.processor.pubsub.message.receiver.PubSubMessageReceiver;
import com.epam.gcp.csv.processor.service.storage.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class GcpPubSubConfig {

  @Bean
  public PubSubInboundChannelAdapter messageChannelAdapter(
      @Value("${spring.cloud.gcp.pub-sub.csv-processor-subscription-name}") String subscriptionName,
      MessageChannel pubSubInputChannel,
      PubSubTemplate pubSubTemplate
  ) {
    PubSubInboundChannelAdapter adapter =
        new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
    adapter.setOutputChannel(pubSubInputChannel);

    log.debug("Subscribed for {}!", subscriptionName);

    return adapter;
  }

  @Bean
  public MessageChannel pubSubInputChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "pubSubInputChannel")
  public MessageHandler messageReceiver(
      ObjectMapper objectMapper,
      ResourceLoader resourceLoader,
      StorageService storageService,
      ThreadPoolTaskExecutor applicationTaskExecutor,
      PubSubMessagePublisher pubSubMessagePublisher
  ) {
    return
        new PubSubMessageReceiver(
            objectMapper,
            resourceLoader,
            storageService,
            applicationTaskExecutor,
            pubSubMessagePublisher
        );
  }

  @Bean
  @ServiceActivator(inputChannel = "pubSubOutputChannel")
  public MessageHandler messageSender(
      @Value("${spring.cloud.gcp.pub-sub.dataflow-json-processing-topic-name}") String topicName,
      PubSubTemplate pubsubTemplate
  ) {
    return new PubSubMessageHandler(pubsubTemplate, topicName);
  }
}
