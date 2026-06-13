package com.signatureapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String email;
    private String name;
    private String role;
}

/*
Why password is in AuthResponse... wait, it's NOT
Notice AuthResponse has email, name, role, token — but no password field. We never send the password back. Not even the hashed one. Sensitive fields stay in the entity, never in responses.
 */