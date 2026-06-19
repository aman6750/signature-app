package com.signatureapp.dto;

import com.signatureapp.model.SigningLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PublicSignViewResponse {

    private String documentFileName;
    private String ownerName;
    private String ownerEmail;
    private String signerName;
    private String signerEmail;
    private LocalDateTime expiresAt;
    private Integer signaturePlacementCount;

    public static PublicSignViewResponse from(SigningLink link, int placementCount) {
        return PublicSignViewResponse.builder()
                .documentFileName(link.getDocument().getFileName())
                .ownerName(link.getCreatedBy().getName())
                .ownerEmail(link.getCreatedBy().getEmail())
                .signerName(link.getSignerName())
                .signerEmail(link.getSignerEmail())
                .expiresAt(link.getExpiresAt())
                .signaturePlacementCount(placementCount)
                .build();
    }
}