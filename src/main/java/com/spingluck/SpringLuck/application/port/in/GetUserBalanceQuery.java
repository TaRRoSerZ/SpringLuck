package com.spingluck.SpringLuck.application.port.in;

public interface GetUserBalanceQuery {
    double getBalance(Long userId);
}
