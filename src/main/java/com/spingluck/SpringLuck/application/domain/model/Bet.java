package com.spingluck.SpringLuck.application.domain.model;

public class Bet {
    private Long id;
    private Long userId;
    private Double amount;
    private String game;
    private BetStatus status; // PENDING, WON, LOST
    private Double gain;
}