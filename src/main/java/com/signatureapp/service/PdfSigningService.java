package com.signatureapp.service;

import com.signatureapp.model.Document;
import com.signatureapp.model.Signature;
import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class PdfSigningService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private String signedDir;

    @PostConstruct
    public void init() {
        try {
            signedDir = uploadDir + "/signed";
            Files.createDirectories(Paths.get(signedDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create signed directory: " + signedDir, e);
        }
    }

    public String signPdf(Document document, List<Signature> signatures, String signatureText) {

        File originalFile = new File(document.getStoragePath());
        if (!originalFile.exists()) {
            throw new RuntimeException("Original PDF file not found: " + document.getStoragePath());
        }

        String signedFileName = UUID.randomUUID().toString() + "-signed.pdf";
        Path signedPath = Paths.get(signedDir, signedFileName);

        //Open the PDF with PDFBox
        try (PDDocument pdfDoc = Loader.loadPDF(originalFile)) {   //Opens a PDF for modification

            for (Signature signature : signatures) {

                int pageIndex = signature.getPageNumber() - 1;

                if (pageIndex < 0 || pageIndex >= pdfDoc.getNumberOfPages()) {
                    continue;
                }

                PDPage page = pdfDoc.getPage(pageIndex);

                try (PDPageContentStream contentStream = new PDPageContentStream(
                        pdfDoc,
                        page,
                        PDPageContentStream.AppendMode.APPEND,  //Add to existing content, don't replace it
                        true,
                        true)) {

                    contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 14);
                    contentStream.setNonStrokingColor(0.0f, 0.0f, 0.6f);

                    contentStream.beginText();  //Enter text mode
                    contentStream.newLineAtOffset(    //Move "cursor" to coordinates
                            signature.getXCoordinate().floatValue(),
                            signature.getYCoordinate().floatValue()
                    );
                    contentStream.showText(signatureText);  //Actually write the text
                    contentStream.endText();  //Exit text mode

                    //Draws a thin rectangle around the signature so it's visually obvious where it was placed. The -5 offsets make the box slightly larger than the text.
                    // stroke() actually draws the outline.
                    contentStream.addRect(
                            signature.getXCoordinate().floatValue() - 5,
                            signature.getYCoordinate().floatValue() - 5,
                            signature.getWidth().floatValue(),
                            signature.getHeight().floatValue()
                    );
                    contentStream.stroke();
                }
            }

            pdfDoc.save(signedPath.toFile());

        } catch (IOException e) {
            throw new RuntimeException("Failed to sign PDF: " + e.getMessage(), e);
        }

        return signedPath.toString();
    }
}