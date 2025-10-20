package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.in.web.BetController;
import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BetController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    BetUseCase betServiceStub;

    @DisplayName("GET /bets")
    @Test
    public void Get_all_bets() throws Exception {
        Bet bet1 = new Bet(1, 100.0, new Date(), false);
        Bet bet2 = new Bet(3, 300.0, new Date(), true);

        Optional<List<Bet>> betsBd = Optional.of(List.of(bet1, bet2));

        when(betServiceStub.getAllBets()).thenReturn(betsBd);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bets");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andExpect(jsonPath("$[0].isWinningBet").value(false))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].amount").value(300.0));

    }

    @DisplayName("GET /bets/{id}")
    @Test
    public void Get_bet_by_id() throws Exception {
        Bet bet1 = new Bet(1, 100.0, new Date(), false);
        when(betServiceStub.getBetById(1)).thenReturn(Optional.of(bet1));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/bets/1");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.isWinningBet").value(false));

    }

    @DisplayName("POST /bets/place")
    @Test
    public void Save_bet() throws Exception {
        String betJson = """
                {
                    "id": 1,
                    "amount": 100.0,
                    "date": "2022-02-02",
                    "isWinningBet": false
                }
                """;

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/bets/place")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(betJson);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated());
    }

}
