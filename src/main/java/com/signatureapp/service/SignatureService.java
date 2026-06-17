package com.signatureapp.service;

import com.signatureapp.dto.PlaceSignatureRequest;
import com.signatureapp.dto.SignatureResponse;
import com.signatureapp.model.Document;
import com.signatureapp.model.Signature;
import com.signatureapp.model.User;
import com.signatureapp.repository.DocumentRepository;
import com.signatureapp.repository.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRepository signatureRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public SignatureResponse placeSignature(PlaceSignatureRequest request, User currentUser) {

        Document document = documentRepository.findByIdAndUploadedBy(request.getDocumentId(), currentUser)
                .orElseThrow(() -> new RuntimeException("Document not found or access denied"));

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

        return SignatureResponse.from(saved);
    }

    public List<SignatureResponse> getSignaturesForDocument(Long documentId, User currentUser) {

        Document document = documentRepository.findByIdAndUploadedBy(documentId, currentUser)
                .orElseThrow(() -> new RuntimeException("Document not found or access denied"));

        return signatureRepository.findByDocument(document).stream()
                .map(SignatureResponse::from)
                .collect(Collectors.toList());
    }
}

