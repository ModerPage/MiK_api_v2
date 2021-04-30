package me.modernpage.security.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegisterRequest {
    private final String username;
    private final String fullname;
    private final String email;
    private final String password;
}
