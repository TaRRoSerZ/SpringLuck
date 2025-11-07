package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserPort {
    Optional<User> saveUser(User user);
    Optional<List<User>> findAllUsers();
    Optional<User> findUserByEmail(String email);
    void makeTransaction(Transaction transaction, User user);
}
