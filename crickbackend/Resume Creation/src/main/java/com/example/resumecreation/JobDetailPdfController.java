package com.example.resumecreation;

import com.example.resumecreation.job_dto.JobDto;
import com.example.resumecreation.job_dto.JobKeyResponsibilityEntity;
import com.example.resumecreation.job_dto.JobRequirementEntity;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/job-details")
public class JobDetailPdfController {
    String fontPath = "static/fonts/montserrat_egular.ttf";
    Color blackRgb = new DeviceRgb(0, 0, 0);
    Color grayRgb = new DeviceRgb(51, 57, 69);
    Color grayRgb2 = new DeviceRgb(218, 224, 226);

    @Autowired
    RestTemplate restTemplate;

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

        ResponseEntity<JobDto> responseEntity = restTemplate.getForEntity("https://web.kakoo-software.com/kakoo-back-end/api/v1/job/job-id/329", JobDto.class);
        JobDto jobDto = responseEntity.getBody();

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


        Paragraph invoiceTitle1 = new Paragraph("Job Title: " + jobDto.getTitle());
        invoiceTitle1.setTextAlignment(TextAlignment.LEFT);
        invoiceTitle1.setFontSize(12);
        invoiceTitle1.setFont(montserratFont);
        invoiceTitle1.setMarginTop(-5);
        document.add(invoiceTitle1);


        Table secondTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        secondTable.setMarginTop(10);
        secondTable.setBorder(Border.NO_BORDER);
        setFomData(secondTable, montserratFont);
        setDetailData(secondTable, montserratFont, jobDto);
        document.add(secondTable);

        addDescription(document, montserratFont, jobDto);

        addJobKeyResponsbilities(document, montserratFont, jobDto);
        addJobRequirements(document, montserratFont, jobDto);

        document.close();
        return baos;
    }

    void setLine(Document document, float right, float left) {
        Table headerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        headerTable.setBorder(new SolidBorder(grayRgb2, 0.1f)); // Set border color and width
        headerTable.setMargins(0, right, 0, left);
        document.add(headerTable);
    }

    void addJobKeyResponsbilities(Document document, PdfFont montserratFont, JobDto jobDto) {
        if (Objects.isNull(jobDto.getJobKeyResponsibilityEntities()) || jobDto.getJobKeyResponsibilityEntities().isEmpty())
            return;

        Paragraph summeryTitle = new Paragraph("Job Key Responsbilities");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(14);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(8);
        document.add(summeryTitle);

        document.add(new Cell().setMargin(5));
        setLine(document, 0, 0);

        List fromList = new List();
        fromList.setListSymbol("\u2022 ");
        for (JobKeyResponsibilityEntity jobKeyResponsibilityEntity : jobDto.getJobKeyResponsibilityEntities()) {
            ListItem companyTitle = new ListItem(jobKeyResponsibilityEntity.getName());
            companyTitle.setTextAlignment(TextAlignment.LEFT);
            companyTitle.setFontSize(8);
            companyTitle.setFont(montserratFont);
            fromList.add(companyTitle);
        }
        Cell fromDetailCell = new Cell();
        fromDetailCell.add(fromList);
        fromDetailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        document.add(fromDetailCell);
    }

    void addJobRequirements(Document document, PdfFont montserratFont, JobDto jobDto) {
        if (Objects.isNull(jobDto.getJobRequirementEntities()) || jobDto.getJobRequirementEntities().isEmpty())
            return;

        Paragraph summeryTitle = new Paragraph("Job Requirements");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(14);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(8);
        document.add(summeryTitle);

        document.add(new Cell().setMargin(5));
        setLine(document, 0, 0);

        List fromList = new List();
        fromList.setListSymbol("\u2022 ");
        for (JobRequirementEntity jobRequirementEntity : jobDto.getJobRequirementEntities()) {
            ListItem companyTitle = new ListItem(jobRequirementEntity.getName());
            companyTitle.setTextAlignment(TextAlignment.LEFT);
            companyTitle.setFontSize(8);
            companyTitle.setFont(montserratFont);
            fromList.add(companyTitle);
        }
        Cell fromDetailCell = new Cell();
        fromDetailCell.add(fromList);
        fromDetailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        document.add(fromDetailCell);
    }

    void addDescription(Document document, PdfFont montserratFont, JobDto jobDto) throws IOException {

        Paragraph summeryTitle = new Paragraph("Description");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(14);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(8);
        document.add(summeryTitle);

        document.add(new Cell().setMargin(5));
        setLine(document, 0, 0);

        org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(jobDto.description);
        processHtmlElementsForSummery(jsoupDocument.body(), document);
    }

    void setFomData(Table table, PdfFont montserratFont) {

        List fromList = new List();
        fromList.setListSymbol("");

        ListItem fromTitle = new ListItem("Company");
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

    void setDetailData(Table table, PdfFont montserratFont, JobDto jobDto) {
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
        setDetailKeyAndValue(invoiceDetailListCell, montserratFont, "Job ID: ", jobDto.getJobId() + "");
        setDetailKeyAndValue(invoiceDetailListCell, montserratFont, "Created By: ","Rupesh Mandal");
        setDetailKeyAndValue(invoiceDetailListCell, montserratFont, "Creation Date: ", jobDto.getCreatedAt().getDayOfMonth() +
                "-" + jobDto.getCreatedAt().getMonthValue() + "-" + jobDto.getCreatedAt().getYear());
        table.addCell(invoiceDetailListCell);
    }

    void setDetailKeyAndValue(Cell invoiceDetailListCell, PdfFont montserratFont, String key, String value) {
        Table headerTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);

        headerTable.addCell(new Cell().add(new Paragraph(key).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER));

        headerTable.addCell(new Cell().add(new Paragraph(value).setFont(montserratFont).setFontSize(8)
                .setFontColor(blackRgb).setTextAlignment(TextAlignment.RIGHT)).setBorder(Border.NO_BORDER));
        invoiceDetailListCell.add(headerTable);

    }


    private void processHtmlElementsForSummery(Element element, Document pdfDocument) throws IOException {
        PdfFont montserratFont = PdfFontFactory.createFont(fontPath);
        for (int i = 0; i < element.childNodes().size(); i++) {
            org.jsoup.nodes.Node node = element.childNodes().get(i);
            if (node instanceof Element) {
                Paragraph summeryDtailsParagraph = new Paragraph();

                Element childElement = (Element) node;

                // For demonstration purposes, let's assume we are only interested in <ul> and <li> tags
                if (childElement.tagName().equals("ul") || childElement.tagName().equals("ol")) {
                    // Handle unordered list
                    processHtmlElementsForSummery(childElement, pdfDocument);
                } else if (childElement.tagName().equals("li")) {
                    // Handle list item
                    summeryDtailsParagraph = new Paragraph("\u2022 " + childElement.text());

                } else {
                    if (i > 0) {
                        summeryDtailsParagraph = new Paragraph(childElement.text());
                    } else {
                        summeryDtailsParagraph = new Paragraph("\n" + childElement.text());
                    }
                }
                summeryDtailsParagraph.setTextAlignment(TextAlignment.LEFT);
                summeryDtailsParagraph.setFontSize(10);
                summeryDtailsParagraph.setFont(montserratFont);
                summeryDtailsParagraph.setMarginTop(-10);
                summeryDtailsParagraph.setPaddingLeft(5);
                summeryDtailsParagraph.setPaddingRight(5);
                pdfDocument.add(summeryDtailsParagraph); // Use bullet point for list items
            }
        }
    }


}
