package com.signatureapp.controller;

import com.signatureapp.dto.DocumentResponse;
import com.signatureapp.dto.PlaceSignatureRequest;
import com.signatureapp.dto.SignWithDataRequest;
import com.signatureapp.dto.SignatureResponse;
import com.signatureapp.model.User;
import com.signatureapp.service.SignatureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;

    @PostMapping
    public ResponseEntity<SignatureResponse> placeSignature(
            @Valid @RequestBody PlaceSignatureRequest request,
            @AuthenticationPrincipal User currentUser) {

        SignatureResponse response = signatureService.placeSignature(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{docId}")
    public ResponseEntity<List<SignatureResponse>> getSignaturesForDocument(
            @PathVariable Long docId,
            @AuthenticationPrincipal User currentUser) {

        List<SignatureResponse> signatures = signatureService.getSignaturesForDocument(docId, currentUser);
        return ResponseEntity.ok(signatures);
    }

    @PostMapping("/finalize")
    public ResponseEntity<DocumentResponse> finalizeSignatures(
            @Valid @RequestBody SignWithDataRequest request,
            @AuthenticationPrincipal User currentUser) {

        DocumentResponse response = signatureService.finalizeSignatures(request, currentUser);
        return ResponseEntity.ok(response);
    }
}