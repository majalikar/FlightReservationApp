package com.FlightReservation.controller;

import com.FlightReservation.payload.PaymentRequest;
import com.stripe.Stripe;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostMapping("/charge")
    public ResponseEntity<String> createCharge(@RequestBody PaymentRequest paymentRequest) {
        try {
            Stripe.apiKey = stripeApiKey;

            // Create a Charge request using the Stripe API
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", paymentRequest.getAmount());
            chargeParams.put("currency", paymentRequest.getCurrency());

            Map<String, Object> cardParams = new HashMap<>();
            cardParams.put("number", paymentRequest.getCardNumber());
            cardParams.put("exp_month", paymentRequest.getExpMonth());
            cardParams.put("exp_year", paymentRequest.getExpYear());
            cardParams.put("cvc", paymentRequest.getCvc());

            chargeParams.put("source", cardParams);

            Charge charge = Charge.create(chargeParams);

            // Process the charge response
            if (charge.getPaid()) {
                // Payment successful
                return ResponseEntity.ok("Payment successful!");
            } else {
                // Payment failed
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed!");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }
}


