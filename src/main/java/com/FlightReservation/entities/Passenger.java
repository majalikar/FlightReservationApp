package com.FlightReservation.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "passengers")
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String mobileNo;

    @ManyToMany(mappedBy = "passengers")
    private List<Flight> flights;

    // Constructors, getters, and setters

    public Passenger() {
    }

    public Passenger(String fullName, String email, String mobileNo) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNo = mobileNo;
    }

    // Other constructors, getters, and setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }
}

