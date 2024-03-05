package com.example.resumecreation.pdf;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@RestController
public class HrOfMonth {
    String fontPath = "static/fonts/montserrat_egular.ttf";
    String fontPath2 = "static/fonts/playfair_display_regular.ttf";
    Color grayRgb2 = new DeviceRgb(218,224,226);

    @GetMapping("/hr-of-month")
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



    private ByteArrayOutputStream generatePDFAndRetern() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfFont montserratFont = PdfFontFactory.createFont(fontPath);
        PdfFont playfairDisplayFont = PdfFontFactory.createFont(fontPath2);

        // Create a PdfWriter object to write to a file
        PdfWriter writer = new PdfWriter(baos);

        // Create a PdfDocument object to represent the PDF
        PdfDocument pdf = new PdfDocument(writer);
        pdf.setDefaultPageSize(new PageSize(800.0F,500.0F));

        // Create a Document object to add elements to the PDF
        Document document = new Document(pdf);


        Image image = new Image(ImageDataFactory.create("/home/rupesh-mandal/IdeaProjects/CrickInformer/crickbackend/Resume Creation/src/main/resources/static/medal.png"));
        image.setMargins(0, 0, 0, 0);
        image.scaleToFit(100f, 100f);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        image.setTextAlignment(TextAlignment.CENTER);
        Cell imageCell = new Cell();
        imageCell.add(image);
        imageCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        imageCell.setTextAlignment(TextAlignment.CENTER);
        document.add(imageCell);



        com.itextpdf.layout.element.Paragraph invoiceTitle1 = new Paragraph("HR of the Month Certificate");
        invoiceTitle1.setTextAlignment(TextAlignment.CENTER);
        invoiceTitle1.setFontSize(22);
        invoiceTitle1.setBold();
        invoiceTitle1.setFont(montserratFont);
        document.add(invoiceTitle1);



        com.itextpdf.layout.element.Paragraph invoiceTitle2 = new Paragraph("PROUDLY PRESENT TO");
        invoiceTitle2.setTextAlignment(TextAlignment.CENTER);
        invoiceTitle2.setFontSize(12);
        invoiceTitle2.setFont(montserratFont);
        document.add(invoiceTitle2);



        com.itextpdf.layout.element.Paragraph invoiceTitle3 = new Paragraph("Faisal Shah");
        invoiceTitle3.setTextAlignment(TextAlignment.CENTER);
        invoiceTitle3.setFontSize(30);
        invoiceTitle3.setBold();
        invoiceTitle3.setFont(playfairDisplayFont);
        document.add(invoiceTitle3);

        setLine(document,100,100);

        com.itextpdf.layout.element.Paragraph invoiceTitle4 = new Paragraph("For being the most productive HR of December");
        invoiceTitle4.setTextAlignment(TextAlignment.CENTER);
        invoiceTitle4.setFontSize(12);
        invoiceTitle4.setFont(montserratFont);
        document.add(invoiceTitle4);

        Table topTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        topTable.setBorder(Border.NO_BORDER);

        Cell designationCell=new Cell();
        designationCell.setBorder(Border.NO_BORDER);
        com.itextpdf.layout.element.Paragraph invoiceTitle5 = new Paragraph("For being the most productive HR of December For being the most productive HR of December For being the most productive HR of December For being the most productive HR of December For being the most productive HR of December");
        invoiceTitle5.setTextAlignment(TextAlignment.LEFT);
        invoiceTitle5.setFontSize(10);
        invoiceTitle5.setFont(montserratFont);
        designationCell.add(invoiceTitle5);
        designationCell.setVerticalAlignment(VerticalAlignment.MIDDLE);


        topTable.addCell(designationCell);

        Cell signatureCell=new Cell();
        Image signetureImage = new Image(ImageDataFactory.create("/home/rupesh-mandal/IdeaProjects/CrickInformer/crickbackend/Resume Creation/src/main/resources/static/signature.jpg"));
        signetureImage.scaleToFit(100f, 100f);
        signetureImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
        signetureImage.setTextAlignment(TextAlignment.CENTER);
        signetureImage.setBorder(Border.NO_BORDER);
        signatureCell.add(signetureImage);
        signatureCell.setBorder(Border.NO_BORDER);
        setLine(signatureCell,50,50);
        signatureCell.add(new Paragraph("Signature: Rakesh Verma").setTextAlignment(TextAlignment.CENTER));

        topTable.addCell(signatureCell);

        document.add(topTable);


        com.itextpdf.layout.element.Paragraph invoiceTitle6 = new Paragraph("Money Core PVT");
        invoiceTitle6.setTextAlignment(TextAlignment.CENTER);
        invoiceTitle6.setFontSize(12);
        invoiceTitle6.setMarginTop(5);
        invoiceTitle6.setFont(playfairDisplayFont);
        document.add(invoiceTitle6);

        document.close();
        return baos;
    }

    void setLine(Document document, float right, float left){
        Table headerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        headerTable.setBorder(new SolidBorder(grayRgb2, 0.5f)); // Set border color and width
        headerTable.setMargins(0,right,0,left);
        document.add(headerTable);
    }
    void setLine(Cell document, float right, float left){
        Table headerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        headerTable.setBorder(new SolidBorder(grayRgb2, 0.5f)); // Set border color and width
        headerTable.setMargins(0,right,0,left);
        document.add(headerTable);
    }

}
