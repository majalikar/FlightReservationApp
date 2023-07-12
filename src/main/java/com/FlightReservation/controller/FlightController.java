package com.FlightReservation.controller;

import com.FlightReservation.entities.Flight;
import com.FlightReservation.entities.Passenger;
import com.FlightReservation.entities.Reservation;
import com.FlightReservation.payload.FlightDTO;
import com.FlightReservation.payload.PassengerDTO;
import com.FlightReservation.repository.FlightRepository;
import com.FlightReservation.repository.PassengerRepository;
import com.FlightReservation.repository.ReservationRepository;
import com.FlightReservation.utils.EmailService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightRepository flightRepository;

    private PassengerRepository passengerRepository;

    private ReservationRepository reservationRepository;

    @Autowired
    public FlightController(FlightRepository flightRepository, PassengerRepository passengerRepository, ReservationRepository reservationRepository) {
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.reservationRepository = reservationRepository;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addFlight(@RequestBody FlightDTO flightDTO) {
        // Create a new Flight entity
        Flight flight = new Flight();
        flight.setFlightNumber(flightDTO.getFlightNumber());
        flight.setOrigin(flightDTO.getOrigin());
        flight.setDestination(flightDTO.getDestination());
        flight.setDepartureDateTime(flightDTO.getDepartureDateTime());
        flight.setArrivalDateTime(flightDTO.getArrivalDateTime());
        // Set other properties as needed

        // Save the flight
        flightRepository.save(flight);

        return ResponseEntity.status(HttpStatus.CREATED).body("Flight added successfully");
    }
    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam("origin") String origin,
            @RequestParam("destination") String destination,
            @RequestParam(value = "departureAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureAfter,
            @RequestParam(value = "departureBefore", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureBefore
    ) {
        List<Flight> flights;

        if (departureAfter != null && departureBefore != null) {
            flights = flightRepository.findByOriginAndDestinationAndDepartureDateTimeBetween(origin, destination, departureAfter, departureBefore);
        } else if (departureAfter != null) {
            flights = flightRepository.findByOriginAndDestinationAndDepartureDateTimeAfter(origin, destination, departureAfter);
        } else {
            flights = flightRepository.findByOriginAndDestination(origin, destination);
        }

        return ResponseEntity.ok(flights);
    }
    @PostMapping("/book")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> bookFlight(@RequestParam("flightId") Long flightId,
                                             @RequestBody List<PassengerDTO> passengerDTOs,
                                             Authentication authentication) {
        Optional<Flight> flightOptional = flightRepository.findById(flightId);
        if (!flightOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flight not found");
        }

        Flight flight = flightOptional.get();

        List<Reservation> reservations = new ArrayList<>();
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (PassengerDTO passengerDTO : passengerDTOs) {
            Passenger passenger = passengerRepository.findByEmail(passengerDTO.getEmail());

            if (passenger == null) {
                // Passenger does not exist, create a new one
                passenger = new Passenger();
                passenger.setFullName(passengerDTO.getFullName());
                passenger.setEmail(passengerDTO.getEmail());
                passenger.setMobileNo(passengerDTO.getMobileNo());
                // Set other properties as needed
                passengerRepository.save(passenger);
            }

            Reservation reservation = new Reservation();
            reservation.setFlight(flight);
            reservation.setPassenger(passenger);
            reservation.setReservationDateTime(currentDateTime);
            reservation.setConfirmed(true);
            reservationRepository.save(reservation);

            reservations.add(reservation);

            // Establish the association from the Flight entity to the Passenger entity
            if (!flight.getPassengers().contains(passenger)) {
                flight.getPassengers().add(passenger);
            }

            // Send SMS notification to the passenger
            sendSMSToPassenger(passenger.getMobileNo(), flight, passengerDTO);
        }

        flightRepository.save(flight);

        User user = (User) authentication.getPrincipal();
        String userEmail = user.getUsername();

        // Send email notifications to user and passengers
        sendEmailToUser(userEmail, flight, passengerDTOs);
        sendEmailToPassengers(flight, passengerDTOs);

        return ResponseEntity.status(HttpStatus.CREATED).body("Flight(s) booked successfully");
    }


    private void sendEmailToUser(String userEmail, Flight flight, List<PassengerDTO> passengers) {
        // Code to send email to the user with flight and reservation details
        // You can use an email service or library to implement this functionality
        // Example code:
        String subject = "Flight Booking Confirmation";
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Thank you for booking the flight. Here are your reservation details:\n\n");
        // Append flight details
        messageBuilder.append("Flight Details:\n");
        messageBuilder.append("Flight Number: ").append(flight.getFlightNumber()).append("\n");
        messageBuilder.append("Origin: ").append(flight.getOrigin()).append("\n");
        messageBuilder.append("Destination: ").append(flight.getDestination()).append("\n");
        messageBuilder.append("Departure Date and Time: ").append(flight.getDepartureDateTime()).append("\n");
        messageBuilder.append("Arrival Date and Time: ").append(flight.getArrivalDateTime()).append("\n");
        // Include other flight details as needed

        // Append passenger details
        messageBuilder.append("\nPassenger Details:\n");
        for (PassengerDTO passengerDTO : passengers) {
            messageBuilder.append("Full Name: ").append(passengerDTO.getFullName()).append("\n");
            messageBuilder.append("Email: ").append(passengerDTO.getEmail()).append("\n");
            messageBuilder.append("Mobile Number: ").append(passengerDTO.getMobileNo()).append("\n");
            messageBuilder.append("\n");
        }

        String message = messageBuilder.toString();


        // Send email to the user
        EmailService.sendEmail(userEmail, subject, message);
    }


    private void sendEmailToPassengers(Flight flight, List<PassengerDTO> passengers) {
        // Code to send email to each passenger with flight and reservation details
        // You can use an email service or library to implement this functionality
        // Example code:
        for (PassengerDTO passengerDTO : passengers) {
            String passengerEmail = passengerDTO.getEmail();
            String subject = "Flight Booking Confirmation";
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("Thank you for booking the flight. Here are your reservation details:\n\n");
            // Append flight details
            messageBuilder.append("Flight Details:\n");
            messageBuilder.append("Flight Number: ").append(flight.getFlightNumber()).append("\n");
            messageBuilder.append("Origin: ").append(flight.getOrigin()).append("\n");
            messageBuilder.append("Destination: ").append(flight.getDestination()).append("\n");
            messageBuilder.append("Departure Date and Time: ").append(flight.getDepartureDateTime()).append("\n");
            messageBuilder.append("Arrival Date and Time: ").append(flight.getArrivalDateTime()).append("\n");
            // Include other flight details as needed

            // Append passenger details
            messageBuilder.append("\nPassenger Details:\n");
            messageBuilder.append("Full Name: ").append(passengerDTO.getFullName()).append("\n");
            messageBuilder.append("Email: ").append(passengerDTO.getEmail()).append("\n");
            messageBuilder.append("Mobile Number: ").append(passengerDTO.getMobileNo()).append("\n");
            messageBuilder.append("\n");

            String message = messageBuilder.toString();

            EmailService.sendEmail(passengerEmail, subject, message);
        }
    }
    private void sendSMSToPassenger(String phoneNumber, Flight flight, PassengerDTO passengerDTO) {
        // Initialize the SMS service provider (Twilio, Nexmo, etc.)
        // Replace the placeholders with your actual account credentials
        String accountSid = "AC21b206b8d7e17063fe3bb7cd4db6b022";
        String authToken = "463e4e9731223a33ed6b8f8000789a9e";
        Twilio.init(accountSid, authToken);

        // Compose the message to be sent
        String message = "Dear " + passengerDTO.getFullName() + ", your flight has been booked. Flight details: " + flight.getFlightNumber() + " - " + flight.getDestination();

        // Specify the sender and recipient phone numbers
        String fromPhoneNumber = "+13612665749";

        try {
            // Send the SMS
            Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(fromPhoneNumber), message).create();
            System.out.println("SMS sent successfully.");
        } catch (Exception e) {
            System.err.println("Failed to send SMS. Error: " + e.getMessage());
        }
    }
}


