package com.service.powercrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.service.powercrm.domain.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}