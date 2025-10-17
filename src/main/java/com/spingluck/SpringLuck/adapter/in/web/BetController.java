package com.spingluck.SpringLuck.adapter.in.web;

import com.spingluck.SpringLuck.application.domain.model.Bet;
import com.spingluck.SpringLuck.application.port.in.BetUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bets")
public class BetController {

    private final BetUseCase betService;

    @Autowired
    public BetController(BetUseCase betService){
        this.betService = betService;
    }

    @GetMapping
    public ResponseEntity<List<Bet>> getALlBets(){
        Optional<List<Bet>> bets = betService.getAllBets();
        return bets.isPresent() ? ResponseEntity.ok(bets.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bet> getBetById(@PathVariable int id){
        Optional<Bet> bet = betService.getBetById(id);
        return bet.isPresent() ? ResponseEntity.ok(bet.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping("/place")
    public ResponseEntity<Void> placeBet(@RequestBody Bet bet){
        try {
            betService.placeBet(bet);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
