package com.signatureapp.repository;

import com.signatureapp.model.Document;
import com.signatureapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByUploadedBy(User user);

    Optional<Document> findByIdAndUploadedBy(Long id, User user);
}