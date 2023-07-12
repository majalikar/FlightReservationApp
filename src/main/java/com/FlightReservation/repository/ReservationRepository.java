package com.FlightReservation.repository;

import com.FlightReservation.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // You can add custom query methods or use the existing ones provided by JpaRepository
}

