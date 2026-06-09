# signature-app
Enterprise-grade document e-signature platform built with Java, Spring Boot, and React. Features secure PDF upload, digital signing, shareable links, email notifications, audit trails, and cloud storage (AWS S3). Containerized with Docker. Inspired by DocuSign, Adobe Sign, and Zoho Sign.

## 🛠️ Tech Stack

**Backend**
- Java 17+, Spring Boot 3.x
- Spring Security + JWT (Authentication)
- Spring Data JPA / Hibernate
- MySQL (Database)
- Maven (Build Tool)

**PDF & File Handling**
- Apache PDFBox (PDF reading, writing, signing)
- Spring MultipartFile (file uploads)

**Email**
- Spring Boot Starter Mail (signing notifications via SMTP)

**Cloud Storage**
- AWS S3 (production document storage)
- Local filesystem (development)

**API & Documentation**
- REST API (JSON)
- Swagger / OpenAPI

**Frontend**
- React + Tailwind CSS
- react-pdf (PDF rendering)
- dnd-kit (drag & drop signatures)

**DevOps**
- Docker (containerization)
- Docker Compose (local multi-service setup)
