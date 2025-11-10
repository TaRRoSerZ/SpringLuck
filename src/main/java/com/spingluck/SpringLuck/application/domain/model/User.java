package com.spingluck.SpringLuck.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class User {
    private UUID id;
    private String email;
    private Double balance;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
