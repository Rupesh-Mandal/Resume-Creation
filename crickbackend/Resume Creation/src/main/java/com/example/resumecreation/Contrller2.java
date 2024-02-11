package com.example.resumecreation;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@RequiredArgsConstructor
@RestController
public class Contrller2 {
    @Autowired
    RestTemplate restTemplate;
//    @GetMapping()
    public ResponseEntity<Void> downloadReport2(HttpServletResponse response)
            throws IOException, DocumentException {

        var resut = generatePDFAndRetern();
        byte[] pdfReport = resut.toByteArray();
        String mimeType = "application/pdf";
        response.setContentType(mimeType);

        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + "report_cv" + ".pdf" + "\""));

        response.setContentLength((int) pdfReport.length);

        InputStream inputStream = new ByteArrayInputStream(pdfReport);

        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    ByteArrayOutputStream generatePDFAndRetern(){
        ResponseEntity<ResumeDataDto> responseEntity = restTemplate.getForEntity("http://157.230.30.255:8080/kakoo-back-end/api/v1/pipeline/get-candidate-resume-for-job/239/candidate-id/230", ResumeDataDto.class);
        ResumeDataDto resumeDataDto = responseEntity.getBody();

        Document document = new Document();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            PdfWriter.getInstance(document,baos);
            document.open();

            String htmlContent = resumeDataDto.aboutContent;
            org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(htmlContent);

            // Process HTML elements and add them to the PDF
            processHtmlElements(jsoupDocument.body(), document);

            document.close();

            System.out.println("PDF generated successfully.");

            return baos;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void processHtmlElements(Element element, Document pdfDocument) throws DocumentException {
        for (org.jsoup.nodes.Node node : element.childNodes()) {
            if (node instanceof Element) {
                Element childElement = (Element) node;

                // For demonstration purposes, let's assume we are only interested in <ul> and <li> tags
                if (childElement.tagName().equals("ul") || childElement.tagName().equals("ol")) {
                    // Handle unordered list
                    processHtmlElements(childElement, pdfDocument);
                } else if (childElement.tagName().equals("li")) {
                    // Handle list item
                    pdfDocument.add(new Paragraph("\u2022 " + childElement.text())); // Use bullet point for list items
                } else {
                    pdfDocument.add(new Paragraph(childElement.text())); // Use bullet point for list items

                    // Handle other HTML elements if needed
//                    processHtmlElements(childElement, pdfDocument);
                }
            }
        }
    }

}
