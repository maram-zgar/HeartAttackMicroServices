package dev.maram.doctor.doctor;

import java.util.UUID;

public record ConsultationCompletedRequest(
        UUID appointmentId,
        UUID patientId,
        String patientEmail,
        String patientFirstName,
        UUID doctorId,
        String doctorEmail
) {
}
