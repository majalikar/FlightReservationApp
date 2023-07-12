package com.FlightReservation.repository;

import com.FlightReservation.entities.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    Passenger findByEmail(String email);
    // You can add custom methods specific to your application's requirements here
}

