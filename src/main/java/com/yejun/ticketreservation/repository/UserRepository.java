package com.yejun.ticketreservation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yejun.ticketreservation.domain.User;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByIdAndActiveTrue(Long userId);

    Optional<User> findByEmail(String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForAccountUpdate(
            @Param("userId") Long userId
    );

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select u from User u where u.id = :userId")
    Optional<User> findByIdForActiveOperation(
            @Param("userId") Long userId
    );

}
