package dev.maram.notification.email;

import dev.maram.notification.kafka.AppointmentEvent;
import dev.maram.notification.kafka.DocWelcomeEvent;
import dev.maram.notification.kafka.WelcomeEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateService templateService;

    @Value("${spring.mail.username:noreply@HeartAttackDetectionClinic.com}")
    private String senderEmail;

    public void sendAppointmentBooked(AppointmentEvent event) {
        sendEmail(
                event.getPatientEmail(),
                "Appointment Created",
                "Dear " + event.getPatientFirstName() + ",\n\n" +
                        "Your appointment has been created at " + event.getHospital() +
                        " on " + event.getDateTime() + ".\n\nThank you."
        );
    }

    public void sendAppointmentCancelled(AppointmentEvent event) {
        sendEmail(
                event.getPatientEmail(),
                "Appointment Cancelled",
                "Dear " + event.getPatientFirstName() + ",\n\n" +
                        "Your appointment at " + event.getHospital() +
                        " on " + event.getDateTime() + " has been cancelled.\n\n" +
                        "Please contact us to reschedule."
        );
    }

    public void sendAppointmentRescheduled(AppointmentEvent event) {
        sendEmail(
                event.getPatientEmail(),
                "Appointment Updated",
                "Dear " + event.getPatientFirstName() + ",\n\n" +
                        "Your appointment at " + event.getHospital() +
                        " has been rescheduled to " + event.getDateTime() + ".\n\nThank you."
        );
    }

    public void sendAppointmentAccepted(AppointmentEvent event) {
        sendEmail(
                event.getPatientEmail(),
                "Appointment Accepted",
                "Dear " + event.getPatientFirstName() + ",\n\n" +
                        "Your appointment at " + event.getHospital() +
                        " on " + event.getDateTime() + " has been accepted.\n\n" +
                        "Please ensure you are able to attend."
        );
    }

    public void sendWelcome(WelcomeEvent event) {
        log.info("sendWelcome called for {} with medicalFileId {}", event.getEmail(), event.getMedicalFileId());
        sendEmail(
                event.getEmail(),
                "Account Creation Succeeded",
                "Dear " + event.getFirstName() + ",\n\n" +
                        "Your account has been successfully created.\n\n" +
                        "Your medical file has been set up and is ready:\n" +
                        "  Medical File ID: " + event.getMedicalFileId() + "\n\n" +
                        "Your file is currently empty and will be filled in by your doctor.\n\n" +
                        "Thank you for joining us."
        );
    }

    public void sendConsultationSummary(String patientEmail, String patientFirstName) {
        sendEmail(
                patientEmail,
                "Consultation Completed",
                "Dear " + patientFirstName + ",\n\n" +
                        "Your consultation has been completed.\n\n" +
                        "Your doctor may have updated your medical file. " +
                        "Please log in to review any changes.\n\n" +
                        "Thank you."
        );
    }

    public void sendDocWelcome(DocWelcomeEvent event) {
        log.info("sendDocWelcome called for Dr. {}", event.getEmail());
        String html = templateService.buildDocWelcome(event);
        sendEmail(event.getEmail(), "Account Creation Succeeded", html);
    }

    private void sendEmail(String to, String subject, String html) {
        try {

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setFrom(senderEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true); // false = plain text
            mailSender.send(mimeMessage);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}