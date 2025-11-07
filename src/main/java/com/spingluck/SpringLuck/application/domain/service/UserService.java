package com.spingluck.SpringLuck.application.domain.service;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.domain.model.User;
import com.spingluck.SpringLuck.application.port.in.UserUseCase;
import com.spingluck.SpringLuck.application.port.out.UserPort;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.yaml.snakeyaml.DumperOptions.LineBreak.WIN;

@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserPort userPort;

    @Override
    public Optional<User> syncUser(User user) {
        Optional<User> existing = userPort.findUserByEmail(user.getEmail());
        if (existing.isPresent()) {
            return existing;
        }
        return userPort.saveUser(user);
    }

    @Override
    public Optional<List<User>> getAllUsers() {
        return userPort.findAllUsers();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userPort.findUserByEmail(email);
    }

    @Override
    public void applyTransaction(User user, TransactionType type, Double amount) {
        Transaction transaction = new Transaction(UUID.randomUUID(), amount, null, user.getId(), type, new Date());
        switch (type) {
            case DEPOSIT, BET_WIN -> user.setBalance(user.getBalance() + amount);
            case WITHDRAW, BET_LOSS, BET_PLACED -> user.setBalance(user.getBalance() - amount);
        }
        user.setUpdatedAt(Instant.now());
        userPort.saveUser(user);
        userPort.makeTransaction(transaction, user);
    }
}
