package com.signatureapp.repository;

import com.signatureapp.model.Document;
import com.signatureapp.model.SigningLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SigningLinkRepository extends JpaRepository<SigningLink, Long> {

    Optional<SigningLink> findByToken(String token);

    List<SigningLink> findByDocument(Document document);
}