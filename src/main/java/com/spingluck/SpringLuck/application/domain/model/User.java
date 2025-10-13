package com.spingluck.SpringLuck.application.domain.model;

import javax.management.relation.Role;

public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private double balance;
    private Role role;
}