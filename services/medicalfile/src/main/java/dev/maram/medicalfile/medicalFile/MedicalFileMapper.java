package dev.maram.medicalfile.medicalFile;

import org.springframework.stereotype.Service;

@Service
public class MedicalFileMapper {

    public MedicalFileResponse toResponse(MedicalFile file) {
        return MedicalFileResponse.builder()
                .id(file.getId())
                .patientId(file.getPatientId())
                .creationDate(file.getCreationDate())
                .lastUpdateDate(file.getLastUpdateDate())
                .riskPercentage(file.getRiskPercentage())
                .riskLevel(file.getRiskLevel())
                //.recommendations(file.getRecommendations())
                .build();
    }
}