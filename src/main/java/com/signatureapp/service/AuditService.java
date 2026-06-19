package com.signatureapp.service;

import com.signatureapp.model.AuditLog;
import com.signatureapp.model.Document;
import com.signatureapp.model.User;
import com.signatureapp.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(User user, Document document, String action, String details) {
        String ipAddress = getClientIp();
        String userAgent = getUserAgent();

        AuditLog logEntry = AuditLog.builder()
                .user(user)
                .document(document)
                .action(action)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(logEntry);
    }

    public List<AuditLog> getAuditLogsForDocument(Long documentId) {
        return auditLogRepository.findByDocumentIdOrderByTimestampDesc(documentId);
    }

    private String getClientIp() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return "UNKNOWN";
            }

            String forwardedFor = request.getHeader("X-Forwarded-For");
            if (forwardedFor != null && !forwardedFor.isEmpty()) {
                return forwardedFor.split(",")[0].trim();
            }

            String ip = request.getRemoteAddr();
            return ip != null ? ip : "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private String getUserAgent() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return "UNKNOWN";
            }
            String userAgent = request.getHeader("User-Agent");  //Returns something like Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/120.0.... Tells you the browser/device. Useful for fraud detection.
            return userAgent != null ? userAgent : "UNKNOWN";
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}