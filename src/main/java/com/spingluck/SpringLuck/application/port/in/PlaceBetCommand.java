package com.spingluck.SpringLuck.application.port.in;

public record PlaceBetCommand(Long userId, double amount, String game) {}

