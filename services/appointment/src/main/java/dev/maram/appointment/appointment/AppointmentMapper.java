package dev.maram.appointment.appointment;

import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {
    public AppointmentResponse toResponse(Appointment request) {
        if (request == null) {
            return null;
        }
        return AppointmentResponse.builder()
                .id(request.getId())
                .patientId(request.getPatientId())
                .doctorId(request.getDoctorId())
                .dateTime(request.getDateTime())
                .hospital(request.getHospital())
                .status(request.getStatus())
                .build();
    }

    public Appointment toEntity(AppointmentRequest request) {
        if (request == null) {
            return null;
        }

        return Appointment.builder()
                .patientId(request.patientId())
                .doctorId(request.doctorId())
                .dateTime(request.dateTime())
                .hospital(request.hospital())
                .status(request.status())
                .build();
    }
}
