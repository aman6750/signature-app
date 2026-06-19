package com.signatureapp.dto;

import com.signatureapp.model.SigningLink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SigningLinkResponse {

    private Long id;
    private String token;
    private String signingUrl;
    private Long documentId;
    private String documentFileName;
    private String signerEmail;
    private String signerName;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;

    public static SigningLinkResponse from(SigningLink link, String baseUrl) {
        return SigningLinkResponse.builder()
                .id(link.getId())
                .token(link.getToken())
                .signingUrl(baseUrl + "/api/public/sign/" + link.getToken())
                .documentId(link.getDocument().getId())
                .documentFileName(link.getDocument().getFileName())
                .signerEmail(link.getSignerEmail())
                .signerName(link.getSignerName())
                .status(link.getStatus())
                .expiresAt(link.getExpiresAt())
                .usedAt(link.getUsedAt())
                .createdAt(link.getCreatedAt())
                .build();
    }
}