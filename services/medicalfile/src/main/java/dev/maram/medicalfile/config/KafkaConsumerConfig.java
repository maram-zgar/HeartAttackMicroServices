package dev.maram.medicalfile.config;

import dev.maram.medicalfile.kafka.ConsultationCreatedEvent;
import dev.maram.medicalfile.kafka.PatientEvent;
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
    public ConsumerFactory<String, PatientEvent> patientConsumerFactory() {
        JsonDeserializer<PatientEvent> deserializer = new JsonDeserializer<>(PatientEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseConfig(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PatientEvent> patientKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PatientEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(patientConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, ConsultationCreatedEvent> consultationConsumerFactory() {
        JsonDeserializer<ConsultationCreatedEvent> deserializer = new JsonDeserializer<>(ConsultationCreatedEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseConfig(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ConsultationCreatedEvent> consultationKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ConsultationCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consultationConsumerFactory());
        return factory;
    }
}