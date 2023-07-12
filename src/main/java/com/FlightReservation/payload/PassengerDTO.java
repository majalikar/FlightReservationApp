package com.FlightReservation.payload;

public class PassengerDTO {

    private String fullName;
    private String email;
    private String mobileNo;

    // Constructors, getters, and setters

    public PassengerDTO() {
    }

    public PassengerDTO(String fullName, String email, String mobileNo) {
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
}

