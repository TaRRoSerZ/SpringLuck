package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.in.web.TransactionController;
import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionStatus;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    TransactionUseCase transactionServiceStub;

    @DisplayName("GET /transactions")
    @Test
    void Get_all_transactions() throws Exception {
        Transaction t1 = new Transaction(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("260e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), "str_1", TransactionType.BET_WIN, TransactionStatus.PENDING, new Date());;
        Transaction t2 = new Transaction(UUID.fromString("170e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("270e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("360e8400-e29b-41d4-a716-446655440000"),"str_2", TransactionType.BET_LOSS, TransactionStatus.PENDING, new Date());

        Optional<List<Transaction>> transactionsBd = Optional.of(List.of(t1, t2));

        when(transactionServiceStub.getAllTransactions()).thenReturn(transactionsBd);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transactions");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].amount").value(500.00))
                .andExpect(jsonPath("$[1].amount").value(500.00))
                .andExpect(jsonPath("$[0].betId").value(UUID.fromString("260e8400-e29b-41d4-a716-446655440000").toString()))
                .andExpect(jsonPath("$[1].betId").value(UUID.fromString("270e8400-e29b-41d4-a716-446655440000").toString()))
                .andExpect(jsonPath("$[0].type").value(TransactionType.BET_WIN.name()))
                .andExpect(jsonPath("$[1].type").value(TransactionType.BET_LOSS.name()));
    }

    @DisplayName("GET /transactions/{id}")
    @Test
    void Get_transaction_by_id() throws Exception {
        Transaction t1 = new Transaction(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("260e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), "str_1", TransactionType.DEPOSIT, TransactionStatus.PENDING, new Date());;

        when(transactionServiceStub.getTransactionById(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"))).thenReturn(Optional.of(t1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transactions/160e8400-e29b-41d4-a716-446655440000");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(UUID.fromString("160e8400-e29b-41d4-a716-446655440000").toString()))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.betId").value(UUID.fromString("260e8400-e29b-41d4-a716-446655440000").toString()))
                .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.name()));
    }

    @DisplayName("GET /transactions/user/{userId}")
    @Test
    void Get_transaction_by_user_id() throws Exception {
        Transaction t1 = new Transaction(UUID.fromString("160e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("260e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("360e8400-e29b-41d4-a716-446655440000"), "str_1", TransactionType.BET_WIN, TransactionStatus.PENDING, new Date());;
        Transaction t2 = new Transaction(UUID.fromString("170e8400-e29b-41d4-a716-446655440000"), 500.0, UUID.fromString("270e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("360e8400-e29b-41d4-a716-446655440000"),"str_2", TransactionType.BET_LOSS, TransactionStatus.PENDING, new Date());

        Optional<List<Transaction>> transactionsBd = Optional.of(List.of(t1, t2));

        when(transactionServiceStub.getAllUserTransaction(UUID.fromString("360e8400-e29b-41d4-a716-446655440000"))).thenReturn(transactionsBd);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transactions/user/360e8400-e29b-41d4-a716-446655440000");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].amount").value(500.00))
                .andExpect(jsonPath("$[1].amount").value(500.00))
                .andExpect(jsonPath("$[0].betId").value(UUID.fromString("260e8400-e29b-41d4-a716-446655440000").toString()))
                .andExpect(jsonPath("$[1].betId").value(UUID.fromString("270e8400-e29b-41d4-a716-446655440000").toString()))
                .andExpect(jsonPath("$[0].type").value(TransactionType.BET_WIN.name()))
                .andExpect(jsonPath("$[1].type").value(TransactionType.BET_LOSS.name()));

    }

    @DisplayName("POST /transactions/create")
    @Test
    void Create_transaction_with_bet() throws Exception {

        Transaction transaction = new Transaction(
                UUID.fromString("170e8400-e29b-41d4-a716-446655440000"),
                100.0,
                UUID.fromString("270e8400-e29b-41d4-a716-446655440000"),
                UUID.fromString("370e8400-e29b-41d4-a716-446655440000"),
                null,
                TransactionType.BET_WIN,
                TransactionStatus.CONFIRMED,
                new Date()
        );

        when(transactionServiceStub.createTransaction(any(Transaction.class)))
                .thenReturn(Optional.of(transaction));

        String transactionJson = """
                {
                    "id": "170e8400-e29b-41d4-a716-446655440000",
                    "amount": 100.0,
                    "betId" : "270e8400-e29b-41d4-a716-446655440000",
                    "userId" : "370e8400-e29b-41d4-a716-446655440000",
                    "type": "BET_WIN",
                    "status": "CONFIRMED",
                    "date": "2022-02-02"
                }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(transactionJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());

    }

    @DisplayName("POST /transactions/create")
    @Test
    void Create_transaction_without_bet() throws Exception {
        Transaction transaction = new Transaction(
                UUID.fromString("180e8400-e29b-41d4-a716-446655440000"),
                100.0,
                null,
                UUID.fromString("380e8400-e29b-41d4-a716-446655440000"),
                "str_123456",
                TransactionType.DEPOSIT,
                TransactionStatus.PENDING,
                new Date()
        );

        when(transactionServiceStub.createTransaction(any(Transaction.class)))
                .thenReturn(Optional.of(transaction));
        String transactionJson = """
                {
                    "id": "180e8400-e29b-41d4-a716-446655440000",
                    "amount": 100.0,
                    "userId" : "380e8400-e29b-41d4-a716-446655440000",
                    "stripe_intent_id" : "str_123456",
                    "type": "DEPOSIT",
                    "status": "PENDING",
                    "date": "2022-02-02"
                }
                """;


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/transactions/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(transactionJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());

    }
}
