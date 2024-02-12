package com.example.resumecreation;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfContentByte;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.itextpdf.text.pdf.PdfTemplate;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.ChartUtilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static javax.swing.text.StyleConstants.Bold;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test-result")
public class TestResultController {
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


        Paragraph testTitle = new Paragraph("Test results of Android app developer");
        testTitle.setTextAlignment(TextAlignment.LEFT);
        testTitle.setFontSize(12);
        testTitle.setFont(montserratFont);
        testTitle.setMarginTop(-5);
        document.add(testTitle);


        Table secondTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        secondTable.setMarginTop(10);
        secondTable.setBorder(Border.NO_BORDER);
        setCandidateDetails(secondTable,montserratFont);
        assessmentDetail(secondTable,montserratFont);
        document.add(secondTable);


        Table thirdTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        thirdTable.setMarginTop(10);
        thirdTable.setBorder(Border.NO_BORDER);
        setCompanyDetails(thirdTable,montserratFont);
        thirdTable.addCell(new Cell().setBorder(Border.NO_BORDER));
        document.add(thirdTable);


        Paragraph summeryTitle = new Paragraph("Candidate Performance Report");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(11);
        summeryTitle.setBold();
        summeryTitle.setFontColor(grayRgb);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(15);
        document.add(summeryTitle);

        setLine(document,0,0);

        Table fourthTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        fourthTable.setBorder(Border.NO_BORDER);
        fourthTable.addCell(addContent());
        fourthTable.addCell(getOtherAssessmentDetail(montserratFont));
        document.add(fourthTable);



        Paragraph wrongAnswerTitle = new Paragraph("Wrong answer by Candidate");
        wrongAnswerTitle.setTextAlignment(TextAlignment.LEFT);
        wrongAnswerTitle.setFontSize(11);
        wrongAnswerTitle.setBold();
        wrongAnswerTitle.setFontColor(grayRgb);
        wrongAnswerTitle.setFont(montserratFont);
        wrongAnswerTitle.setMarginTop(19);
        document.add(wrongAnswerTitle);

        setLine(document,0,0);

        setWrongeAnswer(document,montserratFont);

        document.close();
        return baos;
    }

    void setCompanyDetails(Table table, PdfFont montserratFont) {

        List toList = new List();
        toList.setListSymbol("");

        ListItem fromTitle = new ListItem("Company Details");
        fromTitle.setTextAlignment(TextAlignment.LEFT);
        fromTitle.setFontSize(9);
        fromTitle.setBold();
        fromTitle.setFontColor(grayRgb);
        fromTitle.setFont(montserratFont);
        toList.add(fromTitle);


        ListItem companyTitle = new ListItem("Money Core LLC");
        companyTitle.setTextAlignment(TextAlignment.LEFT);
        companyTitle.setFontSize(8);
        companyTitle.setFont(montserratFont);
        toList.add(companyTitle);


        ListItem emailTitle = new ListItem("<moneycore@gmail.com>");
        emailTitle.setTextAlignment(TextAlignment.LEFT);
        emailTitle.setFontSize(8);
        emailTitle.setFont(montserratFont);
        emailTitle.add(companyTitle);


        ListItem addressTitle = new ListItem("lahan, siraha Lahan Province No. 2 56500 NEPAL");
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

    Cell getOtherAssessmentDetail(PdfFont montserratFont){
        Cell cell=new Cell();

        Table headerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        otherAssessmentKeyAndValue(headerTable,montserratFont,"Total Score","80%");
        otherAssessmentKeyAndValue(headerTable,montserratFont,"Right Answer","8");
        otherAssessmentKeyAndValue(headerTable,montserratFont,"Wrong Answer","2");
        otherAssessmentKeyAndValue(headerTable,montserratFont,"Attempt Answer","10/11");

        cell.setBorder(Border.NO_BORDER);
        cell.add(headerTable);
        return cell;
    }
    void otherAssessmentKeyAndValue(Table headerTable, PdfFont montserratFont, String key, String value){


        Paragraph paragraph1 = new Paragraph(key);
        paragraph1.setTextAlignment(TextAlignment.LEFT);
        paragraph1.setFontSize(10);
        paragraph1.setFont(montserratFont);


        Paragraph paragraph2 = new Paragraph(value);
        paragraph2.setTextAlignment(TextAlignment.LEFT);
        paragraph2.setFontSize(15);
        paragraph2.setBold();
        paragraph2.setFontColor(grayRgb);
        paragraph2.setFont(montserratFont);

        Cell cell=new Cell();
        cell.add(paragraph1);
        cell.add(new Paragraph(""));
        cell.add(paragraph2);

        // Create a table
        Table table = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);
        cell.setBorderRadius(new BorderRadius(10));
        cell.setBorder(Border.NO_BORDER); // Set border color and width
        cell.setBorderRadius(new BorderRadius(5));
        cell.setBackgroundColor(grayRgb2); // Optional background color
        cell.setPadding(10);
        table.addCell(cell);
        table.setMargin(5);

        Cell cell2=new Cell();
        cell2.add(table);
        cell2.setBorder(Border.NO_BORDER);
        headerTable.addCell(cell2);

    }

    void setLine(Document document, float right, float left){
        Table headerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        headerTable.setBorder(new SolidBorder(grayRgb2, 0.1f)); // Set border color and width
        headerTable.setMargins(0,right,0,left);
        document.add(headerTable);
    }

    void setCandidateDetails(Table table, PdfFont montserratFont) {

        List candidateDetailList = new List();
        candidateDetailList.setListSymbol("");

        ListItem fromTitle = new ListItem("Candidate details");
        fromTitle.setTextAlignment(TextAlignment.LEFT);
        fromTitle.setFontSize(9);
        fromTitle.setBold();
        fromTitle.setFontColor(grayRgb);
        fromTitle.setFont(montserratFont);
        candidateDetailList.add(fromTitle);


        ListItem companyTitle = new ListItem("Rupesh Kumar Mandal");
        companyTitle.setTextAlignment(TextAlignment.LEFT);
        companyTitle.setFontSize(8);
        companyTitle.setFont(montserratFont);
        candidateDetailList.add(companyTitle);


        ListItem addressTitle = new ListItem("Java Developer");
        addressTitle.setTextAlignment(TextAlignment.LEFT);
        addressTitle.setFontSize(8);
        addressTitle.setFont(montserratFont);
        addressTitle.setMarginRight(50);
        candidateDetailList.add(addressTitle);

        Cell candidateDetailCell = new Cell();
        candidateDetailCell.add(candidateDetailList);
        candidateDetailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        table.addCell(candidateDetailCell);
    }


    void assessmentDetail(Table table, PdfFont montserratFont) {
        List invoiceDetailList = new List();
        invoiceDetailList.setListSymbol("");

        ListItem detailsTitle = new ListItem("Assessment details");
        detailsTitle.setTextAlignment(TextAlignment.LEFT);
        detailsTitle.setFontSize(9);
        detailsTitle.setBold();
        detailsTitle.setFontColor(grayRgb);
        detailsTitle.setFont(montserratFont);
        invoiceDetailList.add(detailsTitle);

        Cell invoiceDetailListCell = new Cell();
        invoiceDetailListCell.add(invoiceDetailList);
        invoiceDetailListCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        setDetailKeyAndValue(invoiceDetailListCell,montserratFont,"Sent Date: ","February 11, 2024");
        setDetailKeyAndValue(invoiceDetailListCell,montserratFont,"Completed Date: ","February 11, 2024");
//        setDetailKeyAndValue(invoiceDetailListCell,montserratFont,"Assessment ID: ","2343");
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

    private Cell addContent() throws IOException {

        // Create pie chart data
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Correct", 70);
        dataset.setValue("Wronge", 30);

        // Create the pie chart
        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Correct", java.awt.Color.GREEN);
        plot.setSectionPaint("Wronge", java.awt.Color.RED);
        plot.setSectionOutlinesVisible(false);
        chart.setBackgroundPaint(java.awt.Color.WHITE);


        byte[] imageBytes = ChartUtilities.encodeAsPNG(chart.createBufferedImage(500, 400));


        // Create image object from byte array
        Image image = new Image(ImageDataFactory.create(imageBytes));
        image.setMargins(5, 0, 5, 0);
        image.scaleToFit(200f, 200f);
        image.setTextAlignment(TextAlignment.CENTER);
        Cell imageCell = new Cell();
        imageCell.add(image);
        imageCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        imageCell.setTextAlignment(TextAlignment.CENTER);
        return imageCell;
    }



    void setWrongeAnswer(Document document, PdfFont montserratFont){
        Table headerTable = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);
        headerTable.setMarginTop(20);

        headerTable.addCell(new Cell().add(new Paragraph("SN").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Question").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Type").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Given answer").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph("Correct answer").setFont(montserratFont).setFontSize(8).setBold()
                .setFontColor(grayRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        document.add(headerTable);
        setLine(document,5,5);

        wrongAnswerValue(document,montserratFont,"1","What is Java?","MCQ","Java is Database.","Java is programming language.");
        wrongAnswerValue(document,montserratFont,"2","What is Java?","MCQ","Java is Database.","Java is programming language.");
        wrongAnswerValue(document,montserratFont,"3","What is Java?","MCQ","Java is Database.","Java is programming language.");
        wrongAnswerValue(document,montserratFont,"4","What is Java?","MCQ","Java is Database.","Java is programming language.");
        wrongAnswerValue(document,montserratFont,"5","What is Java?","MCQ","Java is Database.","Java is programming language.");
        wrongAnswerValue(document,montserratFont,"6","What is Java?","MCQ","Java is Database.","Java is programming language.");
        wrongAnswerValue(document,montserratFont,"7","What is Java?","MCQ","Java is Database.","Java is programming language.");
        wrongAnswerValue(document,montserratFont,"8","What is Java?","MCQ","Java is Database.","Java is programming language.");
    }


    void wrongAnswerValue(Document document, PdfFont montserratFont,String SN, String question,String type,
                          String givenAnswer, String correctAnswer){
        Table headerTable = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        headerTable.addCell(new Cell().add(new Paragraph(SN).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph(question).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph(type).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph(givenAnswer).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph(correctAnswer).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));

        document.add(headerTable);
    }

}
