package com.yejun.ticketreservation.repository;

import com.yejun.ticketreservation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    //boolean existByEmailAndIdNot(String email, Long id);
}
