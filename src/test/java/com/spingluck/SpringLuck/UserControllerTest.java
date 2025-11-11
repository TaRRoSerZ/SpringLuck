package com.spingluck.SpringLuck;

import com.spingluck.SpringLuck.adapter.in.web.UserController;
import com.spingluck.SpringLuck.application.domain.model.TransactionType;
import com.spingluck.SpringLuck.application.domain.model.User;
import com.spingluck.SpringLuck.application.port.in.UserUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserUseCase userServiceStub;

    @DisplayName("GET /users → retourne tous les utilisateurs")
    @Test
    void get_all_users() throws Exception {
        User u1 = new User(UUID.randomUUID(), "neo@gmail.com", 100.0, true, Instant.now(), Instant.now());
        User u2 = new User(UUID.randomUUID(), "gege@gmail.com", 250.0, true, Instant.now(), Instant.now());

        when(userServiceStub.getAllUsers()).thenReturn(Optional.of(List.of(u1, u2)));

        RequestBuilder request = MockMvcRequestBuilders.get("/users");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").value("neo@gmail.com"))
                .andExpect(jsonPath("$[1].email").value("gege@gmail.com"))
                .andExpect(jsonPath("$[0].balance").value(100.0))
                .andExpect(jsonPath("$[1].balance").value(250.0));
    }

    @DisplayName("GET /users/{user_mail} → retourne un utilisateur par email")
    @Test
    void get_user_by_email() throws Exception {
        User user = new User(UUID.randomUUID(), "neo@gmail.com", 100.0, true, Instant.now(), Instant.now());

        when(userServiceStub.getUserByEmail("neo@gmail.com")).thenReturn(Optional.of(user));

        RequestBuilder request = MockMvcRequestBuilders.get("/users/neo@gmail.com");

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("neo@gmail.com"))
                .andExpect(jsonPath("$.balance").value(100.0))
                .andExpect(jsonPath("$.active").value(true));
    }

    @DisplayName("GET /users/sync → synchronise un utilisateur")
    @Test
    void sync_user() throws Exception {
        String body = """
            {
                "id": "550e8400-e29b-41d4-a716-446655440000",
                "email": "newuser@gmail.com",
                "balance": 0.0,
                "active": true,
                "createdAt": "2025-11-07T00:00:00Z",
                "updatedAt": "2025-11-07T00:00:00Z"
            }
        """;

        User user = new User(
                UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                "newuser@gmail.com",
                0.0,
                true,
                Instant.now(),
                Instant.now()
        );

        when(userServiceStub.syncUser(any(User.class))).thenReturn(Optional.of(user));

        RequestBuilder request = MockMvcRequestBuilders.post("/users/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@gmail.com"));
    }

    @DisplayName("POST /users/transaction → applique une transaction à un utilisateur")
    @Test
    void apply_transaction() throws Exception {
        String email = "gege@gmail.com";
        User user = new User(UUID.randomUUID(), email, 100.0, true, Instant.now(), Instant.now());

        when(userServiceStub.getUserByEmail(email)).thenReturn(Optional.of(user));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/users/transaction")
                .param("email", email)
                .param("type", TransactionType.DEPOSIT.name())
                .param("amount", "50.0");

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @DisplayName("POST /users/transaction → renvoie 404 si utilisateur introuvable")
    @Test
    void apply_transaction_user_not_found() throws Exception {
        when(userServiceStub.getUserByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/users/transaction")
                .param("email", "unknown@gmail.com")
                .param("type", TransactionType.WITHDRAWAL.name())
                .param("amount", "100.0");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}
