package com.FlightReservation.payload;

public class SigninDto {
    private String username;
    private String password;

    // Constructors, getters, and setters

    public SigninDto() {
    }

    public SigninDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

