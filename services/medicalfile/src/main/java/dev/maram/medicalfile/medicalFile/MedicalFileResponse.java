package dev.maram.medicalfile.medicalFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalFileResponse {
    private UUID id;
    private UUID patientId;
    private LocalDate creationDate;
    private LocalDate lastUpdateDate;
    private double riskPercentage;
    private RiskLevel riskLevel;
    //private List<Recommendation> recommendations;
}
