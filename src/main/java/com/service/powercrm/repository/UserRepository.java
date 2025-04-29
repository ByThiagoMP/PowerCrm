package com.service.powercrm.repository;

import com.service.powercrm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}