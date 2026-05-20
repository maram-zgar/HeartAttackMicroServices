package dev.maram.appointment.appointment;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends Neo4jRepository<Appointment, UUID> {

    @Query("""
        MATCH (a:Appointment)
        WHERE a.doctorId = $doctorId
        AND a.dateTime = $dateTime
        AND a.status = $status
        RETURN count(a) > 0
    """)
    boolean existsByDoctorIdAndDateTimeAndStatus(
            UUID doctorId,
            LocalDateTime dateTime,
            AppointmentStatus status
    );

    @Query("""
        MATCH (a:Appointment)
        WHERE a.doctorId = $doctorId
        AND date(datetime(a.dateTime)) = date($date)
        AND a.status <> 'CANCELLED'
        RETURN a
    """)
    List<Appointment> findByDoctorIdAndDate(
            UUID doctorId,
            LocalDate date
    );

    @Query("""
        MATCH (a:Appointment)
        WHERE a.doctorId = $doctorId
        AND date(datetime(a.dateTime)) = date($date)
        AND a.status = $status
        RETURN a
    """)
    List<Appointment> findByDoctorIdAndDateAndStatus(
            UUID doctorId,
            LocalDate date,
            AppointmentStatus status
    );

    List<Appointment> findByPatientId(UUID patientId);
}