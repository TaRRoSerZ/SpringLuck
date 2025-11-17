package com.spingluck.SpringLuck.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Récupère la section "realm_access" du JWT
        var realmAccess = jwt.getClaimAsMap("realm_access");

        if (realmAccess == null) {
            return List.of();
        }

        // Récupère la liste des rôles, la convertit en autorités Spring
        var roles = (List<String>) realmAccess.get("roles");

        return roles == null ? List.of()
                : roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}