package com.spingluck.SpringLuck.adapter.out.presistence;

import com.spingluck.SpringLuck.application.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setBalance(user.getBalance());
        entity.setRole(user.getRole());
        return entity;
    }

    public static User toDomain(UserEntity entity) {
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setPassword(entity.getPassword());
        user.setBalance(entity.getBalance());
        user.setRole(entity.getRole());
        return user;
    }

}
