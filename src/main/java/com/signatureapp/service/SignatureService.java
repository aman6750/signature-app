package com.signatureapp.service;

import com.signatureapp.dto.DocumentResponse;
import com.signatureapp.dto.PlaceSignatureRequest;
import com.signatureapp.dto.SignWithDataRequest;
import com.signatureapp.dto.SignatureResponse;
import com.signatureapp.exception.BadRequestException;
import com.signatureapp.exception.ResourceNotFoundException;
import com.signatureapp.model.Document;
import com.signatureapp.model.Signature;
import com.signatureapp.model.User;
import com.signatureapp.repository.DocumentRepository;
import com.signatureapp.repository.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRepository signatureRepository;
    private final DocumentRepository documentRepository;
    private final PdfSigningService pdfSigningService;
    private final AuditService auditService;

    @Transactional
    public SignatureResponse placeSignature(PlaceSignatureRequest request, User currentUser) {

        Document document = documentRepository.findByIdAndUploadedBy(request.getDocumentId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found or access denied"));

        Signature signature = Signature.builder()
                .document(document)
                .signer(currentUser)
                .pageNumber(request.getPageNumber())
                .xCoordinate(request.getXCoordinate())
                .yCoordinate(request.getYCoordinate())
                .width(request.getWidth())
                .height(request.getHeight())
                .status("PENDING")
                .build();

        Signature saved = signatureRepository.save(signature);

        if (!"PENDING_SIGNATURE".equals(document.getStatus()) && !"SIGNED".equals(document.getStatus())) {
            document.setStatus("PENDING_SIGNATURE");
            documentRepository.save(document);
        }

        auditService.log(currentUser, document, "SIGNATURE_PLACED",
                "Placed signature on page " + request.getPageNumber()
                        + " at (x=" + request.getXCoordinate()
                        + ", y=" + request.getYCoordinate() + ")");

        return SignatureResponse.from(saved);
    }

    public List<SignatureResponse> getSignaturesForDocument(Long documentId, User currentUser) {

        Document document = documentRepository.findByIdAndUploadedBy(documentId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found or access denied"));

        return signatureRepository.findByDocument(document).stream()
                .map(SignatureResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentResponse finalizeSignatures(SignWithDataRequest request, User currentUser) {

        Document document = documentRepository.findByIdAndUploadedBy(request.getDocumentId(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found or access denied"));

        List<Signature> signatures = signatureRepository.findByDocument(document);

        if (signatures.isEmpty()) {
            throw new BadRequestException("No signature placements found. Place at least one signature before finalizing.");
        }

        if ("SIGNED".equals(document.getStatus())) {
            throw new BadRequestException("Document is already signed and cannot be modified.");
        }

        String signedFilePath = pdfSigningService.signPdf(document, signatures, request.getSignatureData());

        document.setSignedFilePath(signedFilePath);
        document.setStatus("SIGNED");
        documentRepository.save(document);

        LocalDateTime now = LocalDateTime.now();
        for (Signature sig : signatures) {
            sig.setStatus("SIGNED");
            sig.setSignedAt(now);
            sig.setSignatureData(request.getSignatureData());
        }
        signatureRepository.saveAll(signatures);

        auditService.log(currentUser, document, "DOCUMENT_SIGNED",
                "Finalized document with signature: '" + request.getSignatureData()
                        + "' across " + signatures.size() + " placement(s)");

        return DocumentResponse.from(document);
    }
}