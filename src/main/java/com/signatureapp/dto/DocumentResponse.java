package com.signatureapp.dto;

import com.signatureapp.model.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class DocumentResponse {

    private Long id;
    private String fileName;
    private String contentType;
    private Long sizeInBytes;
    private String status;
    private String uploadedByName;
    private String uploadedByEmail;
    private LocalDateTime uploadedAt;

    public static DocumentResponse from(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .contentType(document.getContentType())
                .sizeInBytes(document.getSizeInBytes())
                .status(document.getStatus())
                .uploadedByName(document.getUploadedBy().getName())
                .uploadedByEmail(document.getUploadedBy().getEmail())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}