package dev.maram.medicalfile.medicalFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalFileUpdateRequest {
    private RiskLevel riskLevel;
    private Float riskPercentage;
}