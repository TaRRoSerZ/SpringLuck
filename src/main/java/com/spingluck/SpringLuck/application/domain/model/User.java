package com.spingluck.SpringLuck.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.management.relation.Role;

@Getter @Setter
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private double balance;
    private Role role;
}