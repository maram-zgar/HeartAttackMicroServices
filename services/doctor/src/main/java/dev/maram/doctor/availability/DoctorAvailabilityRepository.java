package dev.maram.doctor.availability;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorAvailabilityRepository extends Neo4jRepository<DoctorAvailability, UUID> {

    List<DoctorAvailability> findByDoctorId(UUID doctorId);

    Optional<DoctorAvailability> findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);

    void deleteByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
}