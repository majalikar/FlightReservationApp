package com.FlightReservation.repository;

import com.FlightReservation.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByOriginAndDestination(String origin, String destination);

    List<Flight> findByDepartureDateTimeAfter(LocalDateTime departureDateTime);

    List<Flight> findByDepartureDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Flight> findByOriginAndDestinationAndDepartureDateTimeBetween(String origin, String destination, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Flight> findByOriginAndDestinationAndDepartureDateTimeAfter(String origin, String destination, LocalDateTime departureDateTime);

    // Add more custom search methods as needed
}

