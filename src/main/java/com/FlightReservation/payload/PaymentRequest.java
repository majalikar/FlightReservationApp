package com.FlightReservation.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private String cardNumber;
    private String expMonth;
    private String expYear;
    private String cvc;
    private int amount;
    private String currency;

    // Getters and setters

    // ...
}


