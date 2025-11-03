package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.domain.service.BetService;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
import com.spingluck.SpringLuck.application.port.out.BetPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class BetServiceTest {

    @Test
    void Get_All_Bets() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID betId1 = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID betId2 = UUID.fromString("234e4567-e89b-12d3-a456-426614174000");

        Bet bet1 = new Bet(betId1, userId, 100.0, new Date(), false);
        Bet bet2 = new Bet(betId2, userId, 700.0, new Date(), true);
        List<Bet> betsBd = List.of(bet1, bet2);
        BetPort betPortStub = mock(BetPort.class);
        when(betPortStub.findAll()).thenReturn(Optional.of(betsBd));

        BetUseCase betService = new BetService(betPortStub);
        Optional<List<Bet>> bets = betService.getAllBets();
        if (bets.isEmpty()) {
            return;
        }
        Assertions.assertArrayEquals(betsBd.toArray(), bets.get().toArray());
    }

    @Test
    void Get_Bet_by_id() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID betId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Bet bet2 = new Bet(betId, userId, 700.0, new Date(), true);
        BetPort betPortStub = mock(BetPort.class);
        when(betPortStub.findById(betId)).thenReturn(Optional.of(bet2));

        BetUseCase betService = new BetService(betPortStub);
        Optional<Bet> bet = betService.getBetById(betId);
        if (bet.isEmpty()) {
            return;
        }
        Assertions.assertEquals(Optional.of(bet2), bet);
        Assertions.assertEquals(bet.get().getAmount(), bet2.getAmount());
    }

    @Test
    void Save_Bet() {
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID betId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        Bet bet1 = new Bet(betId,userId, 100.0, new Date(), false);
        BetPort betPortStub = mock(BetPort.class);

        BetUseCase betService = new BetService(betPortStub);
        betService.placeBet(bet1);
        verify(betPortStub).save(bet1);

    }
}
