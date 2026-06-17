package com.signatureapp.repository;

import com.signatureapp.model.Document;
import com.signatureapp.model.Signature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignatureRepository extends JpaRepository<Signature, Long> {

    List<Signature> findByDocument(Document document);

    List<Signature> findByDocumentId(Long documentId);
}
