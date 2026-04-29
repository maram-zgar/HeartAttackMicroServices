package dev.maram.medicalfile.kafka;


import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WelcomeEventProducer {

    private final KafkaTemplate<String, WelcomeEvent> kafkaTemplate;

    public void sendWelcomeEvent(WelcomeEvent event) {
        kafkaTemplate.send("patient.welcome", event);
    }
}
