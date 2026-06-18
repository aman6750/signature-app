package com.signatureapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignWithDataRequest {

    @NotNull(message = "Document ID is required")
    private Long documentId;

    @NotNull(message = "Signature data is required")
    private String signatureData;
}
