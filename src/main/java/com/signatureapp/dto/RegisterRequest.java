package com.signatureapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data   //@Data (Lombok) — Same as before: auto-generates getters, setters, toString, etc. So we don't manually write getName(), setName(...), etc.
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}

/*
Because the API is sending it OUT, not receiving it. We trust ourselves; we don't trust users. Validation is only for incoming data.
What's NOT in the request DTOs (intentionally)

❌ No id field — user can't fake an ID
❌ No role field — user can't make themselves an admin
❌ No createdAt field — user can't fake a registration date

The backend controls all of these. The DTO is a wall. Users can only send what we allow.
 */