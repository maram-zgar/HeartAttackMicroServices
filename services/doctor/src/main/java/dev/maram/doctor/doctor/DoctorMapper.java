package dev.maram.doctor.doctor;

import org.springframework.stereotype.Service;

@Service
public class DoctorMapper {

    public Doctor toDoctor(DoctorRequest request) {
        if (request == null) {
            return null;
        }
        return Doctor.builder()
                .id(request.id())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .numeroRPPS(request.numeroRPPS())
                .hospital(request.hospital())
                .build();
    }

    public DoctorResponse fromDoctor(Doctor doctor) {
        return new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getNumeroRPPS(),
                doctor.getHospital()
        );
    }
}
