package dev.maram.medicalfile.medicalFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalFileResponse {
    private Long id;
    private Long patientId;
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private float riskPercentage;
    private RiskLevel riskLevel;
    //private List<Recommendation> recommendations;
}
