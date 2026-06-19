package com.signatureapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSigningLinkRequest {

    @NotNull(message = "Document ID is required")
    private Long documentId;

    @NotBlank(message = "Signer email is required")
    @Email(message = "Invalid email format")
    private String signerEmail;

    private String signerName;
}