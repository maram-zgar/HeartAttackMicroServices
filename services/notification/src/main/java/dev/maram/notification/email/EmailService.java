package dev.maram.notification.email;


import dev.maram.notification.kafka.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendAppointmentBooked(AppointmentEvent event) {
        sendEmail(
                event.getPatientEmail(),
                "Appointment Confirmed",
                "Dear " + event.getPatientFirstName() + ",\n\n" +
                        "Your appointment has been booked at " + event.getHospital() +
                        " on " + event.getDateTime() + ".\n\n" +
                        "Thank you."
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

    public void sendAppointmentUpdated(AppointmentEvent event) {
        sendEmail(
                event.getPatientEmail(),
                "Appointment Updated",
                "Dear " + event.getPatientFirstName() + ",\n\n" +
                        "Your appointment at " + event.getHospital() +
                        " has been rescheduled to " + event.getDateTime() + ".\n\n" +
                        "Thank you."
        );
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@heartattack.dev");
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
