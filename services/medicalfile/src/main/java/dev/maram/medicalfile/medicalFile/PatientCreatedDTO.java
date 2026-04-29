package dev.maram.medicalfile.medicalFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCreatedDTO {
    private UUID patientId;
    private String firstName;
    private String lastName;
    private String email;
}