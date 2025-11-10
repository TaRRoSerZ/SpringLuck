package com.spingluck.SpringLuck.adapter.in.web;

import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.domain.model.User;
import com.spingluck.SpringLuck.application.port.in.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserUseCase userService;

    @GetMapping
    public ResponseEntity<List<User>> findAllUsers(){
        Optional<List<User>> users = userService.getAllUsers();
        return users.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{user_mail}")
    public ResponseEntity<User> findUserByEmail(@PathVariable String user_mail){
        Optional<User> user = userService.getUserByEmail(user_mail);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/sync")
    public ResponseEntity<User> syncUser(@RequestBody User user){
        Optional<User> userSynced = userService.syncUser(user);
        return userSynced.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/transaction")
    public ResponseEntity<String> applyTransaction(
            @RequestParam String email,
            @RequestParam TransactionType type,
            @RequestParam Double amount
    ) {
        Optional<User> userOpt = userService.getUserByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User with email " + email + " not found");
        }

        User user = userOpt.get();
        userService.applyTransaction(user, type, amount);

        return ResponseEntity.ok("Transaction applied successfully for " + email);
    }

}
