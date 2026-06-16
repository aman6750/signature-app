package com.signatureapp.controller;

import com.signatureapp.dto.DocumentResponse;
import com.signatureapp.model.User;
import com.signatureapp.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {

        DocumentResponse response = documentService.uploadDocument(file, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getMyDocuments(
            @AuthenticationPrincipal User currentUser) {

        List<DocumentResponse> documents = documentService.getMyDocuments(currentUser);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        DocumentResponse document = documentService.getDocumentById(id, currentUser);
        return ResponseEntity.ok(document);
    }
}