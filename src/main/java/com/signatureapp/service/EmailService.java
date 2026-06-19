package com.signatureapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendSigningInvitation(String toEmail, String signerName, String documentName,
                                      String ownerName, String signingLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Please sign: " + documentName);

            String body = "Hi " + (signerName != null ? signerName : "there") + ",\n\n" +
                    ownerName + " has shared a document with you for signing.\n\n" +
                    "Document: " + documentName + "\n\n" +
                    "Click the link below to review and sign:\n" +
                    signingLink + "\n\n" +
                    "This link will expire in 1 day.\n\n" +
                    "Thanks,\nSignature App";

            message.setText(body);
            mailSender.send(message);

            log.info("Signing invitation sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send signing invitation to {}: {}", toEmail, e.getMessage());
        }
    }

    @Async
    public void sendSignedNotification(String ownerEmail, String ownerName,
                                       String signerEmail, String documentName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(ownerEmail);
            message.setSubject("Document signed: " + documentName);

            String body = "Hi " + ownerName + ",\n\n" +
                    "Good news! Your document has been signed.\n\n" +
                    "Document: " + documentName + "\n" +
                    "Signed by: " + signerEmail + "\n\n" +
                    "You can now download the signed PDF from your dashboard.\n\n" +
                    "Thanks,\nSignature App";

            message.setText(body);
            mailSender.send(message);

            log.info("Signed notification sent to owner {}", ownerEmail);
        } catch (Exception e) {
            log.error("Failed to send signed notification to {}: {}", ownerEmail, e.getMessage());
        }
    }
}