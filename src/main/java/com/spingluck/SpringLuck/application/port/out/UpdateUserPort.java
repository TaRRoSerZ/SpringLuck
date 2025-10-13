package com.spingluck.SpringLuck.application.port.out;

import com.spingluck.SpringLuck.application.domain.model.User;

public interface UpdateUserPort {
    void updateUser(User user);
}
