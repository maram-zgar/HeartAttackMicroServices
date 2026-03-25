package dev.maram.patient.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PatientNotFoundException extends RuntimeException{
    private final String msg;
}
