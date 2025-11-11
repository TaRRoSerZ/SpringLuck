package com.spingluck.SpringLuck;


import com.spingluck.SpringLuck.adapter.in.web.StripeController;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionStatus;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StripeController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StripeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionUseCase transactionService;

    @DisplayName("POST /stripe/create-payment-intent - should create payment and return clientSecret")
    @Test
    void createPaymentIntent_shouldReturnClientSecret() throws Exception {

        try (MockedStatic<PaymentIntent> paymentIntentMock = mockStatic(PaymentIntent.class)) {
            PaymentIntent fakeIntent = new PaymentIntent();
            fakeIntent.setId("pi_test_12345");
            fakeIntent.setClientSecret("secret_12345");

            paymentIntentMock.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class))).thenReturn(fakeIntent);

            Transaction transaction = new Transaction(
                    UUID.randomUUID(),
                    100.0,
                    null,
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "pi_test_12345",
                    TransactionType.DEPOSIT,
                    TransactionStatus.PENDING,
                    new Date()
            );

            when(transactionService.createTransaction(any(Transaction.class))).thenReturn(Optional.of(transaction));

            String requestBody = """
                {
                  "amount": 100,
                  "userId": "11111111-1111-1111-1111-111111111111"
                }
            """;

            mockMvc.perform(MockMvcRequestBuilders.post("/stripe/create-payment-intent")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.clientSecret").value("secret_12345"))
                    .andExpect(jsonPath("$.paymentId").value("pi_test_12345"))
                    .andExpect(jsonPath("$.transactionId").exists());
        }
    }

    @DisplayName("POST /stripe/webhook - should confirm payment")
    @Test
    void webhook_shouldConfirmPayment() throws Exception {
        String body = """
        {
          "type": "payment_intent.succeeded",
          "data": {
            "object": {
              "id": "pi_test_abc",
              "customer": "user@example.com"
            }
          }
        }
        """;

        mockMvc.perform(MockMvcRequestBuilders.post("/stripe/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("Received"));

        verify(transactionService).confirmPayment("pi_test_abc", "user@example.com");
    }
}
