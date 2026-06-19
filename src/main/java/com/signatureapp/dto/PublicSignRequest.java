package com.signatureapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PublicSignRequest {

    @NotBlank(message = "Signature data is required")
    private String signatureData;
}