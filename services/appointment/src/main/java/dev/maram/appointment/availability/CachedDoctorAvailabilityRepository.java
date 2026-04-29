package dev.maram.appointment.availability;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.time.DayOfWeek;
import java.util.Optional;
import java.util.UUID;

public interface CachedDoctorAvailabilityRepository extends Neo4jRepository<CachedDoctorAvailability, UUID> {

    Optional<CachedDoctorAvailability> findByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);

    void deleteByDoctorIdAndDayOfWeek(UUID doctorId, DayOfWeek dayOfWeek);
}