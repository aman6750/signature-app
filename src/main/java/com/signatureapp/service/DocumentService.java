package com.signatureapp.service;

import com.signatureapp.dto.DocumentResponse;
import com.signatureapp.model.Document;
import com.signatureapp.model.User;
import com.signatureapp.repository.DocumentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    public DocumentResponse uploadDocument(MultipartFile file, User currentUser) {

        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + ".pdf";
        Path targetPath = Paths.get(uploadDir, uniqueFileName);

        try {
            Files.copy(file.getInputStream(), targetPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file to disk", e);
        }

        Document document = Document.builder()
                .fileName(originalFileName)
                .storagePath(targetPath.toString())
                .contentType(file.getContentType())
                .sizeInBytes(file.getSize())
                .status("UPLOADED")
                .uploadedBy(currentUser)
                .build();

        Document saved = documentRepository.save(document);

        return DocumentResponse.from(saved);
    }

    public List<DocumentResponse> getMyDocuments(User currentUser) {
        return documentRepository.findByUploadedBy(currentUser).stream()
                .map(DocumentResponse::from)
                .collect(Collectors.toList());
    }

    public DocumentResponse getDocumentById(Long id, User currentUser) {
        Document document = documentRepository.findByIdAndUploadedBy(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Document not found or access denied"));
        return DocumentResponse.from(document);
    }


    public org.springframework.core.io.Resource getOriginalFile(Long id, User currentUser) {
        Document document = documentRepository.findByIdAndUploadedBy(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Document not found or access denied"));

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(document.getStoragePath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found on disk");
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Could not load file: " + e.getMessage(), e);
        }
    }

    public org.springframework.core.io.Resource getSignedFile(Long id, User currentUser) {
        Document document = documentRepository.findByIdAndUploadedBy(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Document not found or access denied"));

        if (document.getSignedFilePath() == null) {
            throw new RuntimeException("Document has not been signed yet");
        }

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(document.getSignedFilePath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("Signed file not found on disk");
            }
            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Could not load signed file: " + e.getMessage(), e);
        }
    }

    public Document getDocumentEntity(Long id, User currentUser) {
        return documentRepository.findByIdAndUploadedBy(id, currentUser)
                .orElseThrow(() -> new RuntimeException("Document not found or access denied"));
    }

}