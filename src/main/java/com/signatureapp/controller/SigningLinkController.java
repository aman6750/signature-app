package com.signatureapp.controller;

import com.signatureapp.dto.CreateSigningLinkRequest;
import com.signatureapp.dto.SigningLinkResponse;
import com.signatureapp.model.User;
import com.signatureapp.service.SigningLinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signing-links")
@RequiredArgsConstructor
public class SigningLinkController {

    private final SigningLinkService signingLinkService;

    @PostMapping
    public ResponseEntity<SigningLinkResponse> createSigningLink(
            @Valid @RequestBody CreateSigningLinkRequest request,
            @AuthenticationPrincipal User currentUser) {

        SigningLinkResponse response = signingLinkService.createSigningLink(request, currentUser);
        return ResponseEntity.ok(response);
    }
}