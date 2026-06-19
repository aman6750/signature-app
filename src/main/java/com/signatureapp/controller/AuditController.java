package com.signatureapp.controller;

import com.signatureapp.dto.AuditLogResponse;
import com.signatureapp.exception.ResourceNotFoundException;
import com.signatureapp.model.User;
import com.signatureapp.service.AuditService;
import com.signatureapp.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;
    private final DocumentService documentService;

    @GetMapping("/{docId}")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsForDocument(
            @PathVariable Long docId,
            @AuthenticationPrincipal User currentUser) {

        documentService.getDocumentEntity(docId, currentUser);

        List<AuditLogResponse> logs = auditService.getAuditLogsForDocument(docId).stream()
                .map(AuditLogResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(logs);
    }
}