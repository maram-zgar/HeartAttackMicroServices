package dev.maram.notification.config;

import dev.maram.notification.kafka.AppointmentEvent;
import dev.maram.notification.kafka.DocWelcomeEvent;
import dev.maram.notification.kafka.WelcomeEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;


@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConfig() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"
        );
    }

    @Bean
    public ConsumerFactory<String, AppointmentEvent> appointmentConsumerFactory() {
        JsonDeserializer<AppointmentEvent> deserializer = new JsonDeserializer<>(AppointmentEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseConfig(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentEvent> appointmentKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AppointmentEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(appointmentConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, WelcomeEvent> welcomeConsumerFactory() {
        JsonDeserializer<WelcomeEvent> deserializer = new JsonDeserializer<>(WelcomeEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseConfig(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, WelcomeEvent> welcomeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WelcomeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(welcomeConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DocWelcomeEvent> docWelcomeKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DocWelcomeEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(docWelcomeConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, DocWelcomeEvent> docWelcomeConsumerFactory() {
        JsonDeserializer<DocWelcomeEvent> deserializer = new JsonDeserializer<>(DocWelcomeEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseConfig(), new StringDeserializer(), deserializer);
    }
}