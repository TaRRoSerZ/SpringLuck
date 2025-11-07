package com.spingluck.SpringLuck.application.port.in;

import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserUseCase {
    Optional<User> syncUser(User user);
    Optional<List<User>> getAllUsers();
    Optional<User> getUserByEmail(String email);
    void applyTransaction(User user, TransactionType type, Double amount);
}
