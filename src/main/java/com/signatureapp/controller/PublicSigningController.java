package com.signatureapp.controller;

import com.signatureapp.dto.PublicSignRequest;
import com.signatureapp.dto.PublicSignViewResponse;
import com.signatureapp.model.SigningLink;
import com.signatureapp.repository.SignatureRepository;
import com.signatureapp.service.SigningLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicSigningController {

    private final SigningLinkService signingLinkService;
    private final SignatureRepository signatureRepository;

    @GetMapping("/sign/{token}")
    public ResponseEntity<PublicSignViewResponse> viewSigningPage(@PathVariable String token) {

        SigningLink link = signingLinkService.getValidLinkByToken(token);

        int placementCount = signatureRepository.findByDocument(link.getDocument()).size();

        return ResponseEntity.ok(PublicSignViewResponse.from(link, placementCount));
    }

    @GetMapping("/sign/{token}/document")
    public ResponseEntity<Resource> viewDocumentForSigning(@PathVariable String token) {

        SigningLink link = signingLinkService.getValidLinkByToken(token);

        try {
            Path path = Paths.get(link.getDocument().getStoragePath());
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                throw new RuntimeException("File not found on disk");
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + link.getDocument().getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("Could not load document: " + e.getMessage(), e);
        }
    }

    @PostMapping("/sign/{token}")
    public ResponseEntity<Map<String, Object>> signDocument(
            @PathVariable String token,
            @Valid @RequestBody PublicSignRequest request) {

        signingLinkService.signViaPublicLink(token, request.getSignatureData());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Document signed successfully. The owner has been notified.");
        return ResponseEntity.ok(response);
    }
}