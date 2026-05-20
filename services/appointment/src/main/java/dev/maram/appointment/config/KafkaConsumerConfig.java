package dev.maram.appointment.config;

import dev.maram.appointment.kafka.AppointmentCompletedEvent;
import dev.maram.appointment.kafka.DoctorAvailabilityEvent;
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
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );
    }

    @Bean
    public ConsumerFactory<String, AppointmentCompletedEvent> appointmentCompletedConsumerFactory() {
        JsonDeserializer<AppointmentCompletedEvent> deserializer =
                new JsonDeserializer<>(AppointmentCompletedEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseConfig(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AppointmentCompletedEvent>
    appointmentCompletedKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, AppointmentCompletedEvent>();
        factory.setConsumerFactory(appointmentCompletedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, DoctorAvailabilityEvent> doctorAvailabilityConsumerFactory() {
        JsonDeserializer<DoctorAvailabilityEvent> deserializer =
                new JsonDeserializer<>(DoctorAvailabilityEvent.class);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeHeaders(false);
        return new DefaultKafkaConsumerFactory<>(baseConfig(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DoctorAvailabilityEvent>
    doctorAvailabilityKafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DoctorAvailabilityEvent>();
        factory.setConsumerFactory(doctorAvailabilityConsumerFactory());
        return factory;
    }
}