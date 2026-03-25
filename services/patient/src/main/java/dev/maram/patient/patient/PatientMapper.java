package dev.maram.patient.patient;

import org.springframework.stereotype.Service;

@Service
public class PatientMapper {
    public Patient toPatient(PatientRequest request) {
        if (request == null) {
            return null;
        }
        return Patient.builder()
                .id(request.id())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .age(request.age())
                .email(request.email())
                .hospital(request.hospital())
                .build();
    }

    public PatientResponse fromPatient(Patient patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getAge(),
                patient.getEmail(),
                patient.getHospital()
        );
    }
}
