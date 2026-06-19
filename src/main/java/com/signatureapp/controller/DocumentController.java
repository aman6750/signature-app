package com.signatureapp.controller;

import com.signatureapp.dto.DocumentResponse;
import com.signatureapp.model.Document;
import com.signatureapp.model.User;
import com.signatureapp.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
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

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadOriginal(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        Resource resource = documentService.getOriginalFile(id, currentUser);
        Document document = documentService.getDocumentEntity(id, currentUser);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + document.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/download/signed")
    public ResponseEntity<Resource> downloadSigned(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        Resource resource = documentService.getSignedFile(id, currentUser);
        Document document = documentService.getDocumentEntity(id, currentUser);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)  //Tells the browser/client "this is a PDF file, not JSON." Without this, the browser might try to display it as text.
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"signed-" + document.getFileName() + "\"")
                .body(resource);
    }
}