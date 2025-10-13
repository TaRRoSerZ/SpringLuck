package com.spingluck.SpringLuck.adapter.out.presistence;

import com.spingluck.SpringLuck.application.domain.model.User;
import com.spingluck.SpringLuck.application.port.out.LoadUserPort;
import com.spingluck.SpringLuck.application.port.out.UpdateUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoadUserPort, UpdateUserPort {

    private final UserRepository userRepository;

    @Override
    public Optional<User> loadUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(UserMapper.toEntity(user));
    }
}
