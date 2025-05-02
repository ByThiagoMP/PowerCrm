package com.service.powercrm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.service.powercrm.domain.User;
import com.service.powercrm.domain.Vehicle;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByPlate(String plate);

    List<Vehicle> findByUser(User user);
}
