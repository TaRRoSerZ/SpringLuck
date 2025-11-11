package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.application.domain.model.Transaction;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.domain.model.User;
import com.spingluck.SpringLuck.application.domain.service.UserService;
import com.spingluck.SpringLuck.application.port.in.TransactionUseCase;
import com.spingluck.SpringLuck.application.port.in.UserUseCase;
import com.spingluck.SpringLuck.application.port.out.UserPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Test
    public void get_all_users(){
        User user1 = new User(UUID.randomUUID(), "neo.rbi2709@gmail.com", 100.00, true, Instant.now(), Instant.now());
        User user2 = new User(UUID.randomUUID(), "gege.rbi2709@gmail.com", 200.00, false, Instant.now(), Instant.now());
        Optional<List<User>> userBd = Optional.of(List.of(user1, user2));

        UserPort userPort = mock(UserPort.class);
        TransactionUseCase transactionService = mock(TransactionUseCase.class);
        when(userPort.findAllUsers()).thenReturn(userBd);

        UserUseCase userService = new UserService(userPort, transactionService);

        Optional<List<User>> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return;
        }
        Assertions.assertArrayEquals(users.get().toArray(), userBd.get().toArray());
    }

    @Test
    public void get_user_by_id(){
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        User user = new User(userId, "gege@gmail.com", 100.00, true, Instant.now(), Instant.now());
        UserPort userPort = mock(UserPort.class);
        when(userPort.findUserByEmail("gege@gmail.com")).thenReturn(Optional.of(user));

        TransactionUseCase transactionService = mock(TransactionUseCase.class);
        UserUseCase userService = new UserService(userPort, transactionService);

        Optional<User> userFound = userService.getUserByEmail("gege@gmail.com");
        if (userFound.isEmpty()) {
            return;
        }
        Assertions.assertEquals(userFound.get(), user);
        Assertions.assertEquals(userFound.get().getId(), user.getId());
        Assertions.assertEquals(userFound.get().getEmail(), user.getEmail());
        Assertions.assertEquals(userFound.get().getBalance(), user.getBalance());
    }

    @Test
    public void  syncUser_shouldReturnExistingUser_whenUserAlreadyExists(){
        String userEmail = "gege@gmail.com";
        User user = new User(UUID.randomUUID(), userEmail, 100.00, true, Instant.now(), Instant.now());
        UserPort userPort = mock(UserPort.class);
        when(userPort.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        TransactionUseCase transactionService = mock(TransactionUseCase.class);
        UserUseCase userService = new UserService(userPort, transactionService);

        Optional<User> existingUser = userService.syncUser(user);
        Assertions.assertTrue(existingUser.isPresent());
        Assertions.assertEquals(user, existingUser.get());
        Assertions.assertEquals(user.getId(), existingUser.get().getId());
        Assertions.assertEquals(user.getBalance(), existingUser.get().getBalance());

        verify(userPort, times(1)).findUserByEmail(userEmail);
        verify(userPort, never()).saveUser(any());
    }

    @Test
    public void  syncUser_shouldSaveUser_whenUserDoesNotExist(){
        String userEmail = "newuser@gmail.com";
        User user = new User(UUID.randomUUID(), userEmail, 100.00, true, Instant.now(), Instant.now());
        UserPort userPort = mock(UserPort.class);
        when(userPort.findUserByEmail(userEmail)).thenReturn(Optional.empty());
        when(userPort.saveUser(user)).thenReturn(Optional.of(user));
        TransactionUseCase transactionService = mock(TransactionUseCase.class);
        UserUseCase userService = new UserService(userPort, transactionService);

        Optional<User> existingUser = userService.syncUser(user);

        Assertions.assertTrue(existingUser.isPresent());
        Assertions.assertEquals(user, existingUser.get());
        Assertions.assertEquals(user.getId(), existingUser.get().getId());
        Assertions.assertEquals(user.getBalance(), existingUser.get().getBalance());

        verify(userPort, times(1)).findUserByEmail(userEmail);
        verify(userPort, times(1)).saveUser(user);
    }

    @ParameterizedTest
    @CsvSource({
            "DEPOSIT,100.0,150.0",
            "BET_WIN,100.0,150.0",
            "WITHDRAWAL,100.0,50.0",
            "BET_LOSS,100.0,50.0",
            "BET_PLACED,100.0,50.0"
    })
    public void applyTransaction_shouldUpdateBalanceAccordingToType(String typeName, double initial, double expected) {
        TransactionType type = TransactionType.valueOf(typeName);
        User user = new User(UUID.randomUUID(), "test@gmail.com", initial, true, Instant.now(), Instant.now());
        double amount = 50.0;

        TransactionUseCase transactionService = mock(TransactionUseCase.class);
        UserPort userPort = mock(UserPort.class);
        UserService userService = new UserService(userPort, transactionService);

        userService.applyTransaction(user, type, amount);

        Assertions.assertEquals(expected, user.getBalance());
        verify(transactionService, times(1)).createTransaction(any(Transaction.class));
    }
}
