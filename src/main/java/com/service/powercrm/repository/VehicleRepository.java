package com.service.powercrm.repository;

import com.service.powercrm.model.Vehicle;
import com.service.powercrm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByPlate(String plate);

    List<Vehicle> findByUser(User user);
}
