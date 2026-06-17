package com.signatureapp.dto;

import com.signatureapp.model.Signature;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class SignatureResponse {

    private Long id;
    private Long documentId;
    private String documentFileName;
    private String signerName;
    private String signerEmail;
    private Integer pageNumber;
    private Double xCoordinate;
    private Double yCoordinate;
    private Double width;
    private Double height;
    private String status;
    private LocalDateTime signedAt;
    private LocalDateTime createdAt;

    public static SignatureResponse from(Signature signature) {
        return SignatureResponse.builder()
                .id(signature.getId())
                .documentId(signature.getDocument().getId())
                .documentFileName(signature.getDocument().getFileName())
                .signerName(signature.getSigner().getName())
                .signerEmail(signature.getSigner().getEmail())
                .pageNumber(signature.getPageNumber())
                .xCoordinate(signature.getXCoordinate())
                .yCoordinate(signature.getYCoordinate())
                .width(signature.getWidth())
                .height(signature.getHeight())
                .status(signature.getStatus())
                .signedAt(signature.getSignedAt())
                .createdAt(signature.getCreatedAt())
                .build();
    }
}