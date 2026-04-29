package dev.maram.gateway.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findByRefreshTokenHash(String refreshTokenHash);

    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.revoked = true WHERE s.user.id = :userId AND s.revoked = false")
    void revokedSessionForUser(UUID userId);
}
