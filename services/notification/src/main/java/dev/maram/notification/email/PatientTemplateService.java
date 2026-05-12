package dev.maram.notification.email;

import dev.maram.notification.kafka.WelcomeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class PatientTemplateService {

    private final TemplateEngine templateEngine;

    public String buildPatientWelcome(WelcomeEvent event) {
        Context ctx = new Context();
        ctx.setVariable("firstName",         event.getFirstName());   // ← was missing
        ctx.setVariable("lastName",          event.getLastName());
        ctx.setVariable("email",             event.getEmail());
        ctx.setVariable("temporaryPassword", event.getTemporaryPassword()); // ← was "password"
        ctx.setVariable("medicalFileId",     event.getMedicalFileId());
        return templateEngine.process("email/patient-welcome", ctx);
    }
}
