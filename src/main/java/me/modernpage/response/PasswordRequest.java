package me.modernpage.response;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PasswordRequest {
    private final String email;
    private final String password;
}
