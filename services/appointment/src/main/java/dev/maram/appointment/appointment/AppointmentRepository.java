package dev.maram.appointment.appointment;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AppointmentRepository extends Neo4jRepository<Appointment, Long> {
}
