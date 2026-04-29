package dev.maram.notification.email;


import dev.maram.notification.kafka.DocWelcomeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;

import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateEngine templateEngine;

    public String buildDocWelcome(DocWelcomeEvent event) {
        Context ctx = new Context();
        ctx.setVariable("lastName",   event.getLastName());
        ctx.setVariable("numeroRPPS", event.getNumeroRPPS());
        ctx.setVariable("password",   event.getPassword());
        return templateEngine.process("email/doc-welcome", ctx);
    }
}
