package com.spingluck.SpringLuck.adapter.in.web;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionStatus;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    @Autowired
    private final TransactionUseCase transactionService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(@RequestBody Map<String, Object> data) {
        try {
            long amount = ((Number) data.get("amount")).longValue();
            String userId = (String) data.get("userId");

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setCurrency("eur")
                    .setAmount(amount)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);


            Transaction t1 = new Transaction(UUID.randomUUID(), (double) amount, null, UUID.fromString(userId),
                    intent.getId(), TransactionType.DEPOSIT, TransactionStatus.PENDING, new Date());

            Optional<Transaction> transactionCreated = transactionService.createTransaction(t1);

            if (transactionCreated.isEmpty()) {return ResponseEntity.badRequest().build();}
            Transaction transaction = transactionCreated.get();

            Map<String, String> responseData = new HashMap<>();
            responseData.put("clientSecret", intent.getClientSecret());
            responseData.put("paymentId", intent.getId());
            responseData.put("transactionId", transaction.getId().toString());
            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }


    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody Map<String, Object> eventData) {
        String eventType = (String) eventData.get("type");

        if ("payment_intent.succeeded".equals(eventType)) {
            Map<String, Object> data = (Map<String, Object>) eventData.get("data");
            Map<String, Object> object = (Map<String, Object>) data.get("object");
            String intentId = (String) object.get("id");
            String userEmail = (String) object.get("customer");

            transactionService.confirmPayment(intentId, userEmail);
        }

        return ResponseEntity.ok("Received");
    }

}
