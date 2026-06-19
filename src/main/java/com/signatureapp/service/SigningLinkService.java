package com.signatureapp.service;

import com.signatureapp.dto.CreateSigningLinkRequest;
import com.signatureapp.dto.SigningLinkResponse;
import com.signatureapp.exception.BadRequestException;
import com.signatureapp.exception.ResourceNotFoundException;
import com.signatureapp.model.Document;
import com.signatureapp.model.Signature;
import com.signatureapp.model.SigningLink;
import com.signatureapp.model.User;
import com.signatureapp.repository.DocumentRepository;
import com.signatureapp.repository.SignatureRepository;
import com.signatureapp.repository.SigningLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SigningLinkService {

    private final SigningLinkRepository signingLinkRepository;
    private final DocumentRepository documentRepository;
    private final SignatureRepository signatureRepository;
    private final PdfSigningService pdfSigningService;
    private final EmailService emailService;
    private final AuditService auditService;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.signing-link.expiration-days}")
    private int expirationDays;

    @Transactional
    public SigningLinkResponse createSigningLink(CreateSigningLinkRequest request, User currentUser) {

        Document document = documentRepository.findByIdAndUploadedBy(request.getDocumentId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found or access denied"));

        if ("SIGNED".equals(document.getStatus())) {
            throw new BadRequestException("Cannot share a signing link for an already-signed document");
        }

        List<Signature> existingSignatures = signatureRepository.findByDocument(document);
        if (existingSignatures.isEmpty()) {
            throw new BadRequestException("Place at least one signature on the document before sharing");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expirationDays);

        SigningLink link = SigningLink.builder()
                .token(token)
                .document(document)
                .createdBy(currentUser)
                .signerEmail(request.getSignerEmail())
                .signerName(request.getSignerName())
                .status("PENDING")
                .expiresAt(expiresAt)
                .build();

        SigningLink saved = signingLinkRepository.save(link);

        auditService.log(currentUser, document, "SIGNING_LINK_CREATED",
                "Shared signing link with: " + request.getSignerEmail());

        String signingUrl = baseUrl + "/api/public/sign/" + token;
        emailService.sendSigningInvitation(
                request.getSignerEmail(),
                request.getSignerName(),
                document.getFileName(),
                currentUser.getName(),
                signingUrl);

        return SigningLinkResponse.from(saved, baseUrl);
    }

    public SigningLink getValidLinkByToken(String token) {

        SigningLink link = signingLinkRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid signing link"));

        if (LocalDateTime.now().isAfter(link.getExpiresAt())) {
            link.setStatus("EXPIRED");
            signingLinkRepository.save(link);
            throw new BadRequestException("This signing link has expired");
        }

        if ("USED".equals(link.getStatus())) {
            throw new BadRequestException("This signing link has already been used");
        }

        if ("REVOKED".equals(link.getStatus())) {
            throw new BadRequestException("This signing link has been revoked");
        }

        return link;
    }

    @Transactional
    public void signViaPublicLink(String token, String signatureData) {

        SigningLink link = getValidLinkByToken(token);

        Document document = link.getDocument();
        List<Signature> signatures = signatureRepository.findByDocument(document);

        if (signatures.isEmpty()) {
            throw new BadRequestException("No signature placements found on this document");
        }

        String signedFilePath = pdfSigningService.signPdf(document, signatures, signatureData);

        document.setSignedFilePath(signedFilePath);
        document.setStatus("SIGNED");
        documentRepository.save(document);

        LocalDateTime now = LocalDateTime.now();
        for (Signature sig : signatures) {
            sig.setStatus("SIGNED");
            sig.setSignedAt(now);
            sig.setSignatureData(signatureData);
        }
        signatureRepository.saveAll(signatures);

        link.setStatus("USED");
        link.setUsedAt(now);
        signingLinkRepository.save(link);

        auditService.log(null, document, "DOCUMENT_SIGNED_VIA_LINK",
                "Document signed via public link by: " + link.getSignerEmail());

        emailService.sendSignedNotification(
                link.getCreatedBy().getEmail(),
                link.getCreatedBy().getName(),
                link.getSignerEmail(),
                document.getFileName());
    }
}