package com.signatureapp.repository;

import com.signatureapp.model.AuditLog;
import com.signatureapp.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByDocumentOrderByTimestampDesc(Document document);

    List<AuditLog> findByDocumentIdOrderByTimestampDesc(Long documentId);
}