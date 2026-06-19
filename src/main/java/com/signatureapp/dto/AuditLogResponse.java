package com.signatureapp.dto;

import com.signatureapp.model.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class AuditLogResponse {

    private Long id;
    private String action;
    private String details;
    private String performedByName;
    private String performedByEmail;
    private Long documentId;
    private String documentFileName;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;

    public static AuditLogResponse from(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .action(log.getAction())
                .details(log.getDetails())
                .performedByName(log.getUser() != null ? log.getUser().getName() : null)
                .performedByEmail(log.getUser() != null ? log.getUser().getEmail() : null)
                .documentId(log.getDocument() != null ? log.getDocument().getId() : null)
                .documentFileName(log.getDocument() != null ? log.getDocument().getFileName() : null)
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .timestamp(log.getTimestamp())
                .build();
    }
}