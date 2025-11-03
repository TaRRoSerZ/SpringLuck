package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.in.web.TransactionController;
import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.model.Transaction;
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

import static org.mockito.Mockito.when;
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
        Bet bet1 = new Bet(1, 100.00, new Date(), true);
        Bet bet2 = new Bet(2, 200.00, new Date(), false);
        Transaction t1 = new Transaction(1, 300.00, 1, TransactionType.BET_WIN, new Date());
        Transaction t2 = new Transaction(2, 50.00, 2, TransactionType.BET_LOSS, new Date());

        Optional<List<Transaction>> transactionsBd = Optional.of(List.of(t1, t2));

        when(transactionServiceStub.getAllTransactions()).thenReturn(transactionsBd);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transactions");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].amount").value(300.00))
                .andExpect(jsonPath("$[1].amount").value(50.00))
                .andExpect(jsonPath("$[0].betId").value(1))
                .andExpect(jsonPath("$[1].betId").value(2))
                .andExpect(jsonPath("$[0].type").value(TransactionType.BET_WIN.name()))
                .andExpect(jsonPath("$[1].type").value(TransactionType.BET_LOSS.name()));
    }

    @DisplayName("GET /transactions/{id}")
    @Test
    void Get_transaction_by_id() throws Exception {
        Bet bet1 = new Bet(1, 100.00, new Date(), true);
        Transaction t1 = new Transaction(1, 300.00, 1, TransactionType.BET_WIN, new Date());

        when(transactionServiceStub.getTransactionById(1)).thenReturn(Optional.of(t1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transactions/1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(300.00))
                .andExpect(jsonPath("$.betId").value(1))
                .andExpect(jsonPath("$.type").value(TransactionType.BET_WIN.name()));
    }

    @DisplayName("POST /transactions/create")
    @Test
    void Create_transaction_with_bet() throws Exception {
        String transactionJson = """
                {
                    "id": 1,
                    "amount": 100.0,
                    "betId" : 1,
                    "type": "BET_WIN",
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
        String transactionJson = """
                {
                    "id": 1,
                    "amount": 100.0,
                    "type": "BET_WIN",
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
