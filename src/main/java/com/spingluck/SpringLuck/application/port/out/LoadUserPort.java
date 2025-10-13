package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.User;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> loadUserById(Long id);
}
