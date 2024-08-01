package com.kafka.practice.LibraryEventManagemnetConsumer.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kafka.practice.LibraryEventManagemnetConsumer.model.CustomeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
public class KafkaConsumerConfig {
  @Value("${spring.kafka.consumer.bootstrap-servers}")
  private List<String> servers;

  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;

  @Value("${spring.kafka.consumer.auto-offset-reset}")
  private String consumerOffset;

  @Value("${topic.retry}")
  private String retryTopic;

  @Value("${topic.dlt}")
  private String deadLetterTopic;

  @Autowired KafkaTemplate template;

  @Bean
  public ConsumerFactory<String, String> consumerFactoryPullDataWhenPartitionsUpdates() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
    //props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerOffset);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String>
      kafkaListenerContainerFactoryPullDataWhenPartitionsUpdates() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactoryPullDataWhenPartitionsUpdates());
    factory.setConcurrency(4); // Number of partitions
    factory.setCommonErrorHandler(commonErrorHandler());
    // factory.getContainerProperties().setPollTimeout(3000);
    return factory;
  }

  @Bean
  public ConsumerFactory<String, String> consumerFactoryConsumeInInterval() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    // props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerOffset);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String>
      kafkaListenerconsumerFactoryConsumeInInterval() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactoryConsumeInInterval());
    factory.setConcurrency(4); // Number of partitions
    factory.getContainerProperties().setPollTimeout(3000);
    return factory;
  }

  public DeadLetterPublishingRecoverer publishRecoverer() {

    DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            template,
            (r, e) -> {
              if (e instanceof CustomeException) {
                return new TopicPartition(retryTopic, r.partition());
              } else {
                return new TopicPartition(deadLetterTopic, r.partition());
              }
            });
    return recoverer;
  }

  @Bean
  public CommonErrorHandler commonErrorHandler() {

    DefaultErrorHandler errorHandler =
        new DefaultErrorHandler(publishRecoverer(), new FixedBackOff(5000L, 2));

    // Specify the exceptions to retry
    errorHandler.addRetryableExceptions(CustomeException.class);


    return errorHandler;
  }

  @Bean
  public NewTopic libraryEvents() {
    return TopicBuilder.name(deadLetterTopic).partitions(3).replicas(3).build();
  }
}
