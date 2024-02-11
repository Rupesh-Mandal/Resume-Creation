package com.example.resumecreation;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/invoice")
public class InvoiceController {
    String fontPath = "static/fonts/montserrat_egular.ttf";
    Color blackRgb = new DeviceRgb(0, 0, 0);
    Color grayRgb = new DeviceRgb(51,57,69);
    Color grayRgb2 = new DeviceRgb(218,224,226);


    @GetMapping()
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

        // Create a PdfWriter object to write to a file
        PdfWriter writer = new PdfWriter(baos);

        // Create a PdfDocument object to represent the PDF
        PdfDocument pdf = new PdfDocument(writer);

        // Create a Document object to add elements to the PDF
        Document document = new Document(pdf);
        Table topTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        topTable.setBorder(Border.NO_BORDER);

        PdfFont montserratFont = PdfFontFactory.createFont(fontPath);


        DeviceRgb deviceRgb = new DeviceRgb(249, 190, 143);
        Color backgroundColor = deviceRgb; // Specify RGB values

        DeviceRgb blueRgb = new DeviceRgb(0, 0, 255);
        Color blueColor = blueRgb; // Specify RGB values

        DeviceRgb greenRgb = new DeviceRgb(34, 139, 34);
        Color greenColor = greenRgb; // Specify RGB values


        Image image = new Image(ImageDataFactory.create("/home/rupesh-mandal/IdeaProjects/CrickInformer/crickbackend/Resume Creation/src/main/resources/static/logo_kakoo.png"));
        image.setMargins(5, 0, 5, 0);
        image.scaleToFit(200f, 50f);
        Cell imageCell = new Cell();
        imageCell.add(image);
        imageCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        topTable.addCell(imageCell);
        document.add(topTable);


        Paragraph invoiceTitle1 = new Paragraph("Final invoice for the December 2022 billing period");
        invoiceTitle1.setTextAlignment(TextAlignment.LEFT);
        invoiceTitle1.setFontSize(12);
        invoiceTitle1.setFont(montserratFont);
        invoiceTitle1.setMarginTop(-5);
        document.add(invoiceTitle1);

        Table secondTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        secondTable.setMarginTop(10);
        secondTable.setBorder(Border.NO_BORDER);
        setFomData(secondTable,montserratFont);
        setDetailData(secondTable,montserratFont);
        document.add(secondTable);

        Table thirdTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        thirdTable.setMarginTop(10);
        thirdTable.setBorder(Border.NO_BORDER);
        setToData(thirdTable,montserratFont);
        thirdTable.addCell(new Cell().setBorder(Border.NO_BORDER));
        document.add(thirdTable);

        Paragraph summeryTitle = new Paragraph("Summary");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(14);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(8);
        document.add(summeryTitle);

        document.add(new Cell().setMargin(5));
        setLine(document,0,0);
        document.add(new Cell().setMargin(5));
        setTotalUses(document,montserratFont);
        document.add(new Cell().setMargin(5));
        setLine(document,0,0);

        setBillTitle(document,montserratFont);
        setLine(document,0,0);
        setBillValue(document,montserratFont);


        Paragraph totalDueTitle = new Paragraph("Total due");
        totalDueTitle.setTextAlignment(TextAlignment.LEFT);
        totalDueTitle.setFontSize(14);
        totalDueTitle.setFont(montserratFont);
        totalDueTitle.setBold();
        totalDueTitle.setMarginTop(8);
        document.add(totalDueTitle);
        setLine(document,0,0);

        document.add(new Cell().setMargin(5));
        setTotalDue(document,montserratFont);
//        setTotalDueTitle(document,montserratFont);
//        setLine(document,0,0);
//        setTotalDueValue(document,montserratFont);

        document.close();
        return baos;
    }

    void setTotalUses(Document document, PdfFont montserratFont){
        Table headerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        headerTable.addCell(new Cell().add(new Paragraph("Total usage charges").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("1,600.00 INR").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        document.add(headerTable);

    }

    void setTotalKeyAndValue(Document document, PdfFont montserratFont, String key, String value){
        Table headerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        headerTable.addCell(new Cell().add(new Paragraph(key).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph(value).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        document.add(headerTable);

    }

    void setLine(Document document, float right, float left){
        Table headerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        headerTable.setBorder(new SolidBorder(grayRgb2, 0.1f)); // Set border color and width
        headerTable.setMargins(0,right,0,left);
        document.add(headerTable);
    }

    void setBillTitle(Document document, PdfFont montserratFont){
        Table headerTable = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);
        headerTable.setMarginTop(20);

        headerTable.addCell(new Cell().add(new Paragraph("Plan").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Type").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("User").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Price/User").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Total Ht").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        document.add(headerTable);
    }

    void setTotalDue(Document document, PdfFont montserratFont){
        setTotalKeyAndValue(document,montserratFont,"Total","1,600.00 INR");
        setTotalKeyAndValue(document,montserratFont,"TVA %","20%");
        setTotalKeyAndValue(document,montserratFont,"TVA","400.00 INR");

        Table headerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        headerTable.addCell(new Cell().add(new Paragraph("Total NET").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("2,000.00 INR").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        document.add(headerTable);
    }
//    void setTotalDueTitle(Document document, PdfFont montserratFont){
//        Table headerTable = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
//        headerTable.setBorder(Border.NO_BORDER);
//
//        headerTable.addCell(new Cell().add(new Paragraph("Total").setFont(montserratFont).setFontSize(8).setBold()
//                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//        headerTable.addCell(new Cell().add(new Paragraph("TVA %").setFont(montserratFont).setFontSize(8).setBold()
//                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//        headerTable.addCell(new Cell().add(new Paragraph("TVA").setFont(montserratFont).setFontSize(8).setBold()
//                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//        headerTable.addCell(new Cell().add(new Paragraph("Total NET").setFont(montserratFont).setFontSize(8).setBold()
//                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//
//        document.add(headerTable);
//    }

//    void setTotalDueValue(Document document, PdfFont montserratFont){
//        Table headerTable = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
//        headerTable.setBorder(Border.NO_BORDER);
//
//        headerTable.addCell(new Cell().add(new Paragraph("1,600.00 INR").setFont(montserratFont).setFontSize(8)
//                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//        headerTable.addCell(new Cell().add(new Paragraph("20%").setFont(montserratFont).setFontSize(8)
//                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//        headerTable.addCell(new Cell().add(new Paragraph("400.00 INR").setFont(montserratFont).setFontSize(8)
//                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//        headerTable.addCell(new Cell().add(new Paragraph("2,000.00 INR").setFont(montserratFont).setFontSize(8)
//                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
//
//
//        document.add(headerTable);
//    }

    void setBillValue(Document document, PdfFont montserratFont){
        Table headerTable = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        headerTable.addCell(new Cell().add(new Paragraph("Pro").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Monthly").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("10").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("160.0").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("1,600.00 INR").setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        document.add(headerTable);
    }

    void setFomData(Table table, PdfFont montserratFont) {

        List fromList = new List();
        fromList.setListSymbol("");

        ListItem fromTitle = new ListItem("From");
        fromTitle.setTextAlignment(TextAlignment.LEFT);
        fromTitle.setFontSize(9);
        fromTitle.setBold();
        fromTitle.setFontColor(grayRgb);
        fromTitle.setFont(montserratFont);
        fromList.add(fromTitle);


        ListItem companyTitle = new ListItem("Kakoo Software LLC");
        companyTitle.setTextAlignment(TextAlignment.LEFT);
        companyTitle.setFontSize(8);
        companyTitle.setFont(montserratFont);
        fromList.add(companyTitle);


        ListItem addressTitle = new ListItem("101 Avenue of the Americas, 2nd Floor New York, NY 10013");
        addressTitle.setTextAlignment(TextAlignment.LEFT);
        addressTitle.setFontSize(8);
        addressTitle.setFont(montserratFont);
        addressTitle.setMarginRight(100);
        fromList.add(addressTitle);

        Cell fromDetailCell = new Cell();
        fromDetailCell.add(fromList);
        fromDetailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        table.addCell(fromDetailCell);
    }

    void setToData(Table table, PdfFont montserratFont) {

        List toList = new List();
        toList.setListSymbol("");

        ListItem fromTitle = new ListItem("To");
        fromTitle.setTextAlignment(TextAlignment.LEFT);
        fromTitle.setFontSize(9);
        fromTitle.setBold();
        fromTitle.setFontColor(grayRgb);
        fromTitle.setFont(montserratFont);
        toList.add(fromTitle);


        ListItem companyTitle = new ListItem("Rupesh Mandal");
        companyTitle.setTextAlignment(TextAlignment.LEFT);
        companyTitle.setFontSize(8);
        companyTitle.setFont(montserratFont);
        toList.add(companyTitle);


        ListItem emailTitle = new ListItem("<mandalkali883@gmail.com>");
        emailTitle.setTextAlignment(TextAlignment.LEFT);
        emailTitle.setFontSize(8);
        emailTitle.setFont(montserratFont);
        emailTitle.add(companyTitle);


        ListItem addressTitle = new ListItem("lahan, siraha Lahan Province No. 2 56500\n" +
                "NEPAL");
        addressTitle.setTextAlignment(TextAlignment.LEFT);
        addressTitle.setFontSize(8);
        addressTitle.setFont(montserratFont);
        addressTitle.setMarginRight(100);
        toList.add(addressTitle);

        Cell toDetailCell = new Cell();
        toDetailCell.add(toList);
        toDetailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        table.addCell(toDetailCell);
    }

    void setDetailData(Table table, PdfFont montserratFont) {
        List invoiceDetailList = new List();
        invoiceDetailList.setListSymbol("");

        ListItem detailsTitle = new ListItem("Details");
        detailsTitle.setTextAlignment(TextAlignment.LEFT);
        detailsTitle.setFontSize(9);
        detailsTitle.setBold();
        detailsTitle.setFontColor(grayRgb);
        detailsTitle.setFont(montserratFont);
        invoiceDetailList.add(detailsTitle);


//        ListItem dateListItem = new ListItem("Date: February 11, 2024");
//        dateListItem.setTextAlignment(TextAlignment.LEFT);
//        dateListItem.setFontSize(8);
//        dateListItem.setFont(montserratFont);
//        invoiceDetailList.add(dateListItem);
//
//
//        ListItem invoiceNumberListItem = new ListItem("Invoice No : KAK240211367777");
//        invoiceNumberListItem.setTextAlignment(TextAlignment.LEFT);
//        invoiceNumberListItem.setFontSize(8);
//        invoiceNumberListItem.setFont(montserratFont);
//        invoiceDetailList.add(invoiceNumberListItem);

        Cell invoiceDetailListCell = new Cell();
        invoiceDetailListCell.add(invoiceDetailList);
        invoiceDetailListCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        setDetailKeyAndValue(invoiceDetailListCell,montserratFont,"Date: ","February 11, 2024");
        setDetailKeyAndValue(invoiceDetailListCell,montserratFont,"Invoice No: ","KAK240211367777");
        table.addCell(invoiceDetailListCell);
    }

    void setDetailKeyAndValue(Cell invoiceDetailListCell, PdfFont montserratFont, String key, String value){
        Table headerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        headerTable.addCell(new Cell().add(new Paragraph(key).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph(value).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        invoiceDetailListCell.add(headerTable);

    }

}
