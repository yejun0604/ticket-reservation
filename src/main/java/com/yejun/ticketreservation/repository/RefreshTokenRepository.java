package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.RefreshToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
        update RefreshToken rt
        set rt.revoked = true
        where rt.user.id = :userId
        and rt.revoked = false
        """)
    void revokeAllByUserId(@Param("userId") Long userId);

    @Query("""
    select rt.user.id
    from RefreshToken rt
    where rt.tokenHash = :tokenHash
    """)
    Optional<Long> findUserIdByTokenHash(
            @Param("tokenHash") String tokenHash
    );
}