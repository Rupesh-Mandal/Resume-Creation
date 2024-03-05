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
import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@RequiredArgsConstructor
@RestController
public class ResumeV2Controller {
    String fontPath = "static/fonts/montserrat_egular.ttf";

    @Autowired
    RestTemplate restTemplate;
    Color blackRgb = new DeviceRgb(0, 0, 0);
    Color grayRgb = new DeviceRgb(51, 57, 69);
    Color whiteRgb = new DeviceRgb(255, 255, 255);
    Color grayRgb2 = new DeviceRgb(218, 224, 226);

    @GetMapping("/cv")
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
        ResponseEntity<ResumeDataDto> responseEntity = restTemplate.getForEntity("https://web.kakoo-software.com/kakoo-back-end/api/v1/pipeline/get-candidate-resume-for-job/382/candidate-id/269", ResumeDataDto.class);
        ResumeDataDto resumeDataDto = responseEntity.getBody();
        String outputDateFormat = "MMM dd, yyyy";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Create a PdfWriter object to write to a file
        PdfWriter writer = new PdfWriter(baos);

        // Create a PdfDocument object to represent the PDF
        PdfDocument pdf = new PdfDocument(writer);
        PdfFont montserratFont = PdfFontFactory.createFont(fontPath);

        // Create a Document object to add elements to the PDF
        Document document = new Document(pdf);
        document.setMargins(0, 0, 0, 0);
//        Table topTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
        Table topTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
        topTable.setBorder(Border.NO_BORDER);
        topTable.setWidth(document.getPdfDocument().getDefaultPageSize().getWidth());

        // Add content to the table
        topTable.addCell(getFirstCell(resumeDataDto, montserratFont));
        topTable.addCell(getSecondCell(resumeDataDto, montserratFont));
        // Set the background color of the cell at index 0
        Cell cellAtIndex0 = topTable.getCell(0, 0);
        cellAtIndex0.setBackgroundColor(new DeviceRgb(61, 89, 77)); // Light blue color
        topTable.setMinHeight(document.getPdfDocument().getDefaultPageSize().getHeight() - document.getTopMargin() - document.getBottomMargin());


        document.add(topTable);


        document.close();
        return baos;
    }


    Cell getSecondCell(ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Cell cell = new Cell();
        cell.setPadding(20);
        setCandidateIdAndJobId(cell, resumeDataDto, montserratFont);

        cell.add(new Cell().setMarginTop(20));
        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsFullName()) && resumeDataDto.getIsFullName() &&
                !Objects.isNull(resumeDataDto.getFullName()) && !resumeDataDto.getFullName().isEmpty()) {
            setFullNameDetails(cell, resumeDataDto, montserratFont);
        }
        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isJobTitle) && resumeDataDto.isJobTitle &&
                !Objects.isNull(resumeDataDto.jobTitle) && !resumeDataDto.jobTitle.isEmpty()) {
            setJobTitleDetails(cell, resumeDataDto, montserratFont);
        }

        if (!Objects.isNull(resumeDataDto.isAboutContent) && resumeDataDto.isAboutContent &&
                !Objects.isNull(resumeDataDto.aboutContent) && !resumeDataDto.aboutContent.isEmpty()) {
            setAboutSummery(cell, resumeDataDto, montserratFont);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails) {
            setCandidatePerformans(cell, resumeDataDto, montserratFont);
        }

        if (!Objects.isNull(resumeDataDto.getIsWorkExperienceEntities()) && resumeDataDto.getIsWorkExperienceEntities()) {
            if (!Objects.isNull(resumeDataDto.getWorkExperienceEntities()) && !resumeDataDto.getWorkExperienceEntities().isEmpty()) {
                setExperience(cell, resumeDataDto, montserratFont);
            }
        }

        if (!Objects.isNull(resumeDataDto.getIsProjectPortfolioEntities()) && resumeDataDto.getIsProjectPortfolioEntities()) {
            if (!Objects.isNull(resumeDataDto.getProjectPortfolioEntities()) && !resumeDataDto.getProjectPortfolioEntities().isEmpty()) {
                setRecentProject(cell, resumeDataDto, montserratFont);
            }
        }
        if (!Objects.isNull(resumeDataDto.isOtherSkill) && resumeDataDto.isOtherSkill &&
                !Objects.isNull(resumeDataDto.otherSkill) && !resumeDataDto.otherSkill.isEmpty()) {
            setOtherSkills(cell, resumeDataDto, montserratFont);
        }

        if (!Objects.isNull(resumeDataDto.getIsCustomDetails()) && resumeDataDto.getIsCustomDetails()) {
            if (!Objects.isNull(resumeDataDto.getCustomDetails()) && !resumeDataDto.getCustomDetails().isEmpty()) {
                setOtherDetails(cell, resumeDataDto, montserratFont);
            }
        }

        return cell;
    }

    private void setOtherDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph summeryTitle = new Paragraph("Other Details");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(11);
        summeryTitle.setBold();
        summeryTitle.setFontColor(grayRgb);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(15);
        cell.add(summeryTitle);
        setLine(cell, 0, 0);

        for (ResumeDataDto.CustomDetails customDetail : resumeDataDto.getCustomDetails()) {
            if (!Objects.isNull(customDetail.fieldName) && !customDetail.fieldName.isEmpty()) {
                String title = customDetail.fieldName;

                Paragraph titleParagraph = new Paragraph(title);
                titleParagraph.setTextAlignment(TextAlignment.LEFT);
                titleParagraph.setBold();
                titleParagraph.setFontSize(9);
                titleParagraph.setFont(montserratFont);
                titleParagraph.setPaddingLeft(5);
                titleParagraph.setPaddingRight(5);
                titleParagraph.setFontColor(grayRgb);
                Paragraph combinedParagraph = new Paragraph();
                combinedParagraph.add(titleParagraph);
                combinedParagraph.setMarginTop(-3);

                if (!Objects.isNull(customDetail.activityName) && !customDetail.activityName.isEmpty()) {
                    Paragraph paragraph2 = new Paragraph(" - " + customDetail.activityName);
                    paragraph2.setFont(montserratFont);
                    paragraph2.setItalic();
                    paragraph2.setFontColor(grayRgb);
                    paragraph2.setFontSize(9);
                    combinedParagraph.add(paragraph2);
                }
                cell.add(combinedParagraph);

            }
            if (!Objects.isNull(customDetail.getDescription()) && !customDetail.getDescription().isEmpty()) {
                Paragraph descriptionParagraph = new Paragraph(Jsoup.parse(customDetail.getDescription()).text());
                descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                descriptionParagraph.setFontSize(8);
                descriptionParagraph.setFont(montserratFont);
                descriptionParagraph.setPaddingLeft(5);
                descriptionParagraph.setPaddingRight(5);
                descriptionParagraph.setMarginTop(-3);
                descriptionParagraph.setFontColor(grayRgb);
                cell.add(descriptionParagraph);

            }
        }
    }

    private void setOtherSkills(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph summeryTitle = new Paragraph("Other Skills");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(11);
        summeryTitle.setBold();
        summeryTitle.setFontColor(grayRgb);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(15);
        cell.add(summeryTitle);
        setLine(cell, 0, 0);

        org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(resumeDataDto.otherSkill);
        try {
            processHtmlElementsForSkill(jsoupDocument.body(), cell);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void processHtmlElementsForSkill(Element element, Cell pdfDocument) throws IOException {
        PdfFont montserratFont = PdfFontFactory.createFont(fontPath);
        for (int i = 0; i < element.childNodes().size(); i++) {
            org.jsoup.nodes.Node node = element.childNodes().get(i);
            if (node instanceof Element) {
                Paragraph summeryDtailsParagraph = new Paragraph();

                Element childElement = (Element) node;

                // For demonstration purposes, let's assume we are only interested in <ul> and <li> tags
                if (childElement.tagName().equals("ul") || childElement.tagName().equals("ol")) {
                    // Handle unordered list
                    processHtmlElementsForSkill(childElement, pdfDocument);
                } else if (childElement.tagName().equals("li")) {
                    // Handle list item
                    summeryDtailsParagraph = new Paragraph("\u2022 " + childElement.text());

                } else {
                    summeryDtailsParagraph = new Paragraph("\u2022 " + childElement.text());
                }
                summeryDtailsParagraph.setTextAlignment(TextAlignment.LEFT);
                summeryDtailsParagraph.setFontSize(8);
                summeryDtailsParagraph.setFont(montserratFont);
                summeryDtailsParagraph.setMarginTop(-2);
                summeryDtailsParagraph.setPaddingLeft(5);
                summeryDtailsParagraph.setPaddingRight(5);
                summeryDtailsParagraph.setFontColor(grayRgb);
                pdfDocument.add(summeryDtailsParagraph); // Use bullet point for list items
            }
        }
    }


    private void setRecentProject(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph summeryTitle = new Paragraph("Recent Project");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(11);
        summeryTitle.setBold();
        summeryTitle.setFontColor(grayRgb);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(15);
        cell.add(summeryTitle);

        setLine(cell, 0, 0);
        for (ResumeDataDto.ProjectPortfolioEntity projectPortfolioEntity : resumeDataDto.getProjectPortfolioEntities()) {
            Cell marginCell = new Cell();
            marginCell.setMarginTop(4);
            cell.add(marginCell);

            if (!Objects.isNull(projectPortfolioEntity.isProjectTitle) && projectPortfolioEntity.isProjectTitle &&
                    !Objects.isNull(projectPortfolioEntity.projectTitle) && !projectPortfolioEntity.projectTitle.isEmpty()) {
                Paragraph titleParagraph = new Paragraph(projectPortfolioEntity.projectTitle);
                titleParagraph.setTextAlignment(TextAlignment.LEFT);
                titleParagraph.setBold();
                titleParagraph.setFontSize(9);
                titleParagraph.setFont(montserratFont);
                titleParagraph.setPaddingLeft(5);
                titleParagraph.setPaddingRight(5);
                titleParagraph.setFontColor(grayRgb);
                cell.add(titleParagraph);
            }
            if (!Objects.isNull(projectPortfolioEntity.isProjectDescription) && projectPortfolioEntity.isProjectDescription &&
                    !Objects.isNull(projectPortfolioEntity.projectDescription) && !projectPortfolioEntity.projectDescription.isEmpty()) {

                Paragraph descriptionParagraph = new Paragraph(Jsoup.parse(projectPortfolioEntity.projectDescription).text());
                descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                descriptionParagraph.setFontSize(8);
                descriptionParagraph.setFont(montserratFont);
                descriptionParagraph.setPaddingLeft(5);
                descriptionParagraph.setPaddingRight(5);
                descriptionParagraph.setMarginTop(-3);
                descriptionParagraph.setFontColor(grayRgb);
                cell.add(descriptionParagraph);

            }
        }
    }

    private void setExperience(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph summeryTitle = new Paragraph("Experience");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(11);
        summeryTitle.setBold();
        summeryTitle.setFontColor(grayRgb);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(15);
        cell.add(summeryTitle);

        setLine(cell, 0, 0);
        String outputDateFormat = "MMM dd, yyyy";


        for (ResumeDataDto.WorkExperienceEntity workExperienceEntity : resumeDataDto.getWorkExperienceEntities()) {
            Cell marginCell = new Cell();
            marginCell.setMarginTop(4);
            cell.add(marginCell);

            if (!Objects.isNull(workExperienceEntity.isJobTitle) && workExperienceEntity.isJobTitle ||
                    !Objects.isNull(workExperienceEntity.isCompanyName) && workExperienceEntity.isCompanyName) {
                String employmentHistoryTitle = "";
                if (!Objects.isNull(workExperienceEntity.isJobTitle) && workExperienceEntity.isJobTitle &&
                        !Objects.isNull(workExperienceEntity.jobTitle) && !workExperienceEntity.jobTitle.isEmpty()) {
                    employmentHistoryTitle = workExperienceEntity.jobTitle;
                }

                if (!Objects.isNull(workExperienceEntity.isCompanyName) && workExperienceEntity.isCompanyName &&
                        !Objects.isNull(workExperienceEntity.companyName) && !workExperienceEntity.companyName.isEmpty()) {
                    employmentHistoryTitle = employmentHistoryTitle + " at " + workExperienceEntity.companyName;
                }
                Paragraph employmentHistoryTitleParagraph = new Paragraph(employmentHistoryTitle);
                employmentHistoryTitleParagraph.setTextAlignment(TextAlignment.LEFT);
                employmentHistoryTitleParagraph.setBold();
                employmentHistoryTitleParagraph.setFontSize(9);
                employmentHistoryTitleParagraph.setFont(montserratFont);
                employmentHistoryTitleParagraph.setPaddingLeft(5);
                employmentHistoryTitleParagraph.setFontColor(grayRgb);
                employmentHistoryTitleParagraph.setPaddingRight(5);
                cell.add(employmentHistoryTitleParagraph);
            }

            String startDate = "";
            String endDate = "";
            String startAndEndDate = "";
            String workTypeAndEmploymentType = "";

            if (!Objects.isNull(workExperienceEntity.isStartDate) && workExperienceEntity.isStartDate) {
                try {
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    SimpleDateFormat outputDateFormatObject = new SimpleDateFormat(outputDateFormat);
                    Date date = inputDateFormat.parse(workExperienceEntity.startDate);
                    outputDateFormatObject.setTimeZone(TimeZone.getTimeZone("UTC"));
                    startDate = outputDateFormatObject.format(date);
                } catch (Exception e) {
                }

            }
            if (!Objects.isNull(workExperienceEntity.isEndDate) && workExperienceEntity.isEndDate) {
                try {
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                    SimpleDateFormat outputDateFormatObject = new SimpleDateFormat(outputDateFormat);
                    Date date = inputDateFormat.parse(workExperienceEntity.endDate);
                    outputDateFormatObject.setTimeZone(TimeZone.getTimeZone("UTC"));
                    endDate = outputDateFormatObject.format(date);
                } catch (Exception e) {
                }
            }
            startAndEndDate = startDate + " - " + endDate;

            if (!Objects.isNull(workExperienceEntity.isEmploymentType) && workExperienceEntity.isEmploymentType
                    && !Objects.isNull(workExperienceEntity.isWorkType) && workExperienceEntity.isWorkType) {
                workTypeAndEmploymentType = "(" + workExperienceEntity.employmentType + " - " + workExperienceEntity.workType + ")";
            }
            if ((!Objects.isNull(workExperienceEntity.isEmploymentType) && workExperienceEntity.isEmploymentType)
                    && (Objects.isNull(workExperienceEntity.isWorkType) || !workExperienceEntity.isWorkType)) {
                workTypeAndEmploymentType = "(" + workExperienceEntity.employmentType + ")";
            }
            if ((Objects.isNull(workExperienceEntity.isEmploymentType) || !workExperienceEntity.isEmploymentType)
                    && (!Objects.isNull(workExperienceEntity.isWorkType) && workExperienceEntity.isWorkType)) {
                workTypeAndEmploymentType = "(" + workExperienceEntity.workType + ")";
            }
            String title = startAndEndDate + " " + workTypeAndEmploymentType;
            if (!Objects.isNull(workExperienceEntity.isLocation) && workExperienceEntity.isLocation &&
                    !Objects.isNull(workExperienceEntity.location) && !workExperienceEntity.location.isEmpty()) {
                title = title + " - " + workExperienceEntity.location;
            }
            Paragraph titleParagraph = new Paragraph(title);
            titleParagraph.setTextAlignment(TextAlignment.LEFT);
            titleParagraph.setFontSize(9);
            titleParagraph.setFont(montserratFont);
            titleParagraph.setPaddingLeft(5);
            titleParagraph.setPaddingRight(5);
            titleParagraph.setMarginTop(-5);
            titleParagraph.setFontColor(grayRgb);
            cell.add(titleParagraph);

            if (!Objects.isNull(workExperienceEntity.isDescription) && workExperienceEntity.isDescription &&
                    !Objects.isNull(workExperienceEntity.description) && !workExperienceEntity.description.isEmpty()) {

                try {
                    org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(workExperienceEntity.description);
                    processHtmlElementsForSummery(jsoupDocument.body(), cell, -3, 5, 5);
                } catch (Exception e) {
                    Paragraph descriptionParagraph = new Paragraph(Jsoup.parse(workExperienceEntity.description).text());
                    descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                    descriptionParagraph.setFontSize(8);
                    descriptionParagraph.setFont(montserratFont);
                    descriptionParagraph.setPaddingLeft(5);
                    descriptionParagraph.setPaddingRight(5);
                    descriptionParagraph.setFontColor(grayRgb);
                    descriptionParagraph.setMarginTop(-3);
                    cell.add(descriptionParagraph);
                }
            }


        }

    }

    private void setCandidatePerformans(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph summeryTitle = new Paragraph("Candidate Report");
        summeryTitle.setTextAlignment(TextAlignment.LEFT);
        summeryTitle.setFontSize(11);
        summeryTitle.setBold();
        summeryTitle.setFontColor(grayRgb);
        summeryTitle.setFont(montserratFont);
        summeryTitle.setMarginTop(15);
        cell.add(summeryTitle);

        setLine(cell, 0, 0);


        Table headerTable = new Table(UnitValue.createPercentArray(3)).useAllAvailableWidth();
        headerTable.setBorder(Border.NO_BORDER);
        headerTable.setMarginTop(5);

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isTotalExperience) && resumeDataDto.isTotalExperience &&
                !Objects.isNull(resumeDataDto.totalExperience)) {
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Experience", resumeDataDto.totalExperience + " Years");
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isAvailability) && resumeDataDto.isAvailability &&
                !Objects.isNull(resumeDataDto.availability) && !resumeDataDto.availability.isEmpty()) {
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Availability", resumeDataDto.availability);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isEmploymentTypeEntity) && resumeDataDto.isEmploymentTypeEntity &&
                !Objects.isNull(resumeDataDto.getEmploymentTypeEntity()) &&
                !Objects.isNull(resumeDataDto.getEmploymentTypeEntity().name) && !resumeDataDto.getEmploymentTypeEntity().name.isEmpty()) {
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Employment Type", resumeDataDto.getEmploymentTypeEntity().name);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isDesiredWorkType) && resumeDataDto.isDesiredWorkType &&
                !Objects.isNull(resumeDataDto.desiredWorkType) && !resumeDataDto.desiredWorkType.isEmpty()) {
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Work Type", resumeDataDto.desiredWorkType);
        }


        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isNationality) && resumeDataDto.isNationality &&
                !Objects.isNull(resumeDataDto.nationality) && !resumeDataDto.nationality.isEmpty()) {
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Nationality", resumeDataDto.nationality);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isLocation) && resumeDataDto.isLocation &&
                !Objects.isNull(resumeDataDto.location) && !resumeDataDto.location.isEmpty()) {
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Location", resumeDataDto.location);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isScoreV) && resumeDataDto.isScoreV &&
                !Objects.isNull(resumeDataDto.getScoreV())) {
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Assessment Score", resumeDataDto.getScoreV() + "%");
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isEvaluation) && resumeDataDto.isEvaluation &&
                !Objects.isNull(resumeDataDto.getEvaluation()) && resumeDataDto.getEvaluation() > 0) {
            String s = "";
            for (Integer i = 0; i < resumeDataDto.getEvaluation(); i++) {
                s = s + "* ";
            }
            otherAssessmentKeyAndValue(headerTable, montserratFont, "Evaluation", s);

        }
        cell.setBorder(Border.NO_BORDER);
        cell.add(headerTable);
    }

    void otherAssessmentKeyAndValue(Table headerTable, PdfFont montserratFont, String key, String value) {
        Paragraph paragraph1 = new Paragraph(key);
        paragraph1.setTextAlignment(TextAlignment.LEFT);
        paragraph1.setFontSize(8);
        paragraph1.setFont(montserratFont);


        Paragraph paragraph2 = new Paragraph(value);
        paragraph2.setTextAlignment(TextAlignment.LEFT);
        paragraph2.setFontSize(8);
        paragraph2.setBold();
        paragraph2.setFontColor(grayRgb);
        paragraph2.setFont(montserratFont);

        Cell cell = new Cell();
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
        cell.setPaddings(4, 8, 4, 8);
        table.addCell(cell);
        table.setMargin(2);

        Cell cell2 = new Cell();
        cell2.add(table);
        cell2.setBorder(Border.NO_BORDER);
        headerTable.addCell(cell2);

    }

    private void setAboutSummery(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        String value="Payment Technical Support Analyst à Market Pay pour Carrefour :\\no Gestion et analyse des incidents et demandes de services dans le respect des SLA ;\\no Monitoring des flux (Grafana), détection et analyse des anomalies/incidents ;\\no Animation des conférences de crise, mobilisation des équipes et coordination de la résolution;\\no Suivi, et communication en permanence aux clients des incidents majeurs; Réalisation des analyses\\nd'impact technique et financier ;\\no Analyse, investigation et réponse aux sollicitations du client;\\no Participation aux réunions périodiques avec le client et les fournisseurs (Worldline, banques ...) ; Rédaction\\ndes comptes rendus;\\no Surveillance de l'activité /Analyse des Alertes ;\\no Evolution des outils de monitoring et ticketing existants (Jira) tout en respectant les priorités ;\\no Rédaction des procédures Run, guide utilisateurs ...\\no Grande autonomie sur le périmètre ;";
        org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(value);
        try {
            processHtmlElementsForSummery(jsoupDocument.body(), cell, -10, 0, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processHtmlElementsForSummery(Element element, Cell cell, float marginTo, float paddingLeft, float paddingRight) throws IOException {
        PdfFont montserratFont = PdfFontFactory.createFont(fontPath);
        for (int i = 0; i < element.childNodes().size(); i++) {
            org.jsoup.nodes.Node node = element.childNodes().get(i);
            if (node instanceof Element) {
                Paragraph summeryDtailsParagraph = new Paragraph();

                Element childElement = (Element) node;

                // For demonstration purposes, let's assume we are only interested in <ul> and <li> tags
                if (childElement.tagName().equals("ul") || childElement.tagName().equals("ol")) {
                    // Handle unordered list
                    processHtmlElementsForSummery(childElement, cell, marginTo, paddingLeft, paddingRight);
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
                summeryDtailsParagraph.setFontSize(8);
                summeryDtailsParagraph.setFont(montserratFont);
                summeryDtailsParagraph.setFontColor(grayRgb);
                summeryDtailsParagraph.setMarginTop(marginTo);
                summeryDtailsParagraph.setPaddingLeft(paddingLeft);
                summeryDtailsParagraph.setPaddingRight(paddingRight);
                cell.add(summeryDtailsParagraph); // Use bullet point for list items
            }
        }
    }


    private void setJobTitleDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph jobTitleParagraph = new Paragraph(resumeDataDto.jobTitle);
        jobTitleParagraph.setTextAlignment(TextAlignment.LEFT);
        jobTitleParagraph.setFontSize(14);
        jobTitleParagraph.setMarginTop(-5);
        jobTitleParagraph.setFontColor(grayRgb);
        jobTitleParagraph.setFont(montserratFont);
        cell.add(jobTitleParagraph);
    }

    private void setFullNameDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph profileParagraph = new Paragraph(resumeDataDto.getFullName());
        profileParagraph.setTextAlignment(TextAlignment.LEFT);
        profileParagraph.setFontSize(18);
        profileParagraph.setMarginTop(10);
        profileParagraph.setBold();
        profileParagraph.setFontColor(grayRgb);
        profileParagraph.setFont(montserratFont);
        cell.add(profileParagraph);
    }

    private void setCandidateIdAndJobId(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph candidateDataParagraph = new Paragraph("Candidate -#" + 122 + " , Job - #" + 234);
        candidateDataParagraph.setTextAlignment(TextAlignment.LEFT);
        candidateDataParagraph.setFontSize(10);
        candidateDataParagraph.setFont(montserratFont);
        cell.add(candidateDataParagraph);

    }


    Cell getFirstCell(ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Cell cell = new Cell();
        cell.setPadding(10);
        try {
            setCandidateImage(resumeDataDto, cell);
        } catch (Exception e) {

        }
        setContactDetails(cell, resumeDataDto, montserratFont);

        if (!Objects.isNull(resumeDataDto.getIsCandidateEducationEntities()) && resumeDataDto.getIsCandidateEducationEntities()) {
            if (!Objects.isNull(resumeDataDto.getCandidateEducationEntities()) && !resumeDataDto.getCandidateEducationEntities().isEmpty()) {
                setEducationDetails(cell, resumeDataDto, montserratFont);
            }
        }


        if (!Objects.isNull(resumeDataDto.getIsCandidateCertificateEntities()) && resumeDataDto.getIsCandidateCertificateEntities()) {
            if (!Objects.isNull(resumeDataDto.getCandidateCertificateEntities()) && !resumeDataDto.getCandidateCertificateEntities().isEmpty()) {
                setCertificationDetails(cell, resumeDataDto, montserratFont);
            }
        }

        if (!Objects.isNull(resumeDataDto.isSkillListEntities) && resumeDataDto.isSkillListEntities &&
                !Objects.isNull(resumeDataDto.getSkillListEntities()) && !resumeDataDto.getSkillListEntities().isEmpty()) {
            setExpertiesDetails(cell, resumeDataDto, montserratFont);
        }


        if (!Objects.isNull(resumeDataDto.isLanguageEntities) && resumeDataDto.isLanguageEntities) {
            if (!Objects.isNull(resumeDataDto.languageEntities) && resumeDataDto.languageEntities.size() > 0) {
                Boolean isNameAvailable = false;
                for (ResumeDataDto.LanguageEntity languageEntity : resumeDataDto.languageEntities) {
                    if (!Objects.isNull(languageEntity.getIsName()) && languageEntity.getIsName() &&
                            !Objects.isNull(languageEntity.getName()) && !languageEntity.getName().isEmpty()) {
                        isNameAvailable = true;
                    }
                }
                if (isNameAvailable) {
                    setSetLanguageDetails(cell, resumeDataDto, montserratFont);
                }
            }
        }


        return cell;
    }

    private void setExpertiesDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph expertiesTitle = new Paragraph("Expertise");
        expertiesTitle.setTextAlignment(TextAlignment.LEFT);
        expertiesTitle.setFontSize(11);
        expertiesTitle.setBold();
        expertiesTitle.setFontColor(whiteRgb);
        expertiesTitle.setFont(montserratFont);
        expertiesTitle.setMarginTop(5);
        cell.add(expertiesTitle);

        setLine(cell, 0, 0);

        List list = new List();
        list.setListSymbol("\u2022 ");

        for (ResumeDataDto.CandidateSkillEntity skillListEntity : resumeDataDto.getSkillListEntities()) {
            String name = Jsoup.parse(skillListEntity.name).text();
            ListItem valueTitle = new ListItem(name);
            valueTitle.setTextAlignment(TextAlignment.LEFT);
            valueTitle.setFontSize(8);
            valueTitle.setFontColor(whiteRgb);
            valueTitle.setFont(montserratFont);
            list.add(valueTitle);
        }
        Cell detailCell = new Cell();
        detailCell.add(list);
        detailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        cell.add(detailCell);
    }

    private void setSetLanguageDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {
        Paragraph expertiesTitle = new Paragraph("Language");
        expertiesTitle.setTextAlignment(TextAlignment.LEFT);
        expertiesTitle.setFontSize(11);
        expertiesTitle.setBold();
        expertiesTitle.setFontColor(whiteRgb);
        expertiesTitle.setFont(montserratFont);
        expertiesTitle.setMarginTop(5);
        cell.add(expertiesTitle);

        setLine(cell, 0, 0);

        List list = new List();
        list.setListSymbol("\u2022 ");


        for (ResumeDataDto.LanguageEntity languageEntity : resumeDataDto.languageEntities) {
            if (!Objects.isNull(languageEntity.getIsName()) && languageEntity.getIsName()) {
                String title = languageEntity.getName();
                String name = Jsoup.parse(title).text();
                ListItem valueTitle = new ListItem(name);
                valueTitle.setTextAlignment(TextAlignment.LEFT);
                valueTitle.setFontSize(8);
                valueTitle.setFontColor(whiteRgb);
                valueTitle.setFont(montserratFont);
                list.add(valueTitle);
            }
        }
        Cell detailCell = new Cell();
        detailCell.add(list);
        detailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        cell.add(detailCell);
    }

    private void setCertificationDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {

        Paragraph educationTitle = new Paragraph("Certification");
        educationTitle.setTextAlignment(TextAlignment.LEFT);
        educationTitle.setFontSize(11);
        educationTitle.setBold();
        educationTitle.setFontColor(whiteRgb);
        educationTitle.setFont(montserratFont);
        educationTitle.setMarginTop(5);
        cell.add(educationTitle);

        setLine(cell, 0, 0);

        for (ResumeDataDto.CandidateCertificateEntity candidateCertificateEntity : resumeDataDto.getCandidateCertificateEntities()) {
            String key = "";
            String value = "";

            if (!Objects.isNull(candidateCertificateEntity.getIsName()) && candidateCertificateEntity.getIsName() &&
                    !Objects.isNull(candidateCertificateEntity.getName()) && !candidateCertificateEntity.getName().isEmpty()) {
                key = candidateCertificateEntity.getName();
            }
            if (!Objects.isNull(candidateCertificateEntity.getIsInstitution()) && candidateCertificateEntity.getIsInstitution()) {
                String title = "";
                title = candidateCertificateEntity.getInstitution();
                if (!Objects.isNull(candidateCertificateEntity.getIsYear()) && candidateCertificateEntity.getIsYear() &&
                        !Objects.isNull(candidateCertificateEntity.getYear()) && !candidateCertificateEntity.getYear().isEmpty()) {
                    title = title + ", " + candidateCertificateEntity.getYear();
                }
                value = title;
            }
            setKeyAndValue(key, value, null, cell, montserratFont);
        }
    }

    private void setEducationDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {

        Paragraph educationTitle = new Paragraph("Education");
        educationTitle.setTextAlignment(TextAlignment.LEFT);
        educationTitle.setFontSize(11);
        educationTitle.setBold();
        educationTitle.setFontColor(whiteRgb);
        educationTitle.setFont(montserratFont);
        educationTitle.setMarginTop(5);
        cell.add(educationTitle);

        setLine(cell, 0, 0);

        for (ResumeDataDto.CandidateEducationEntity candidateEducationEntity : resumeDataDto.getCandidateEducationEntities()) {
            String key = "";
            String value = "";
            String year = "";

            if (!Objects.isNull(candidateEducationEntity.getIsDegreeName()) && candidateEducationEntity.getIsDegreeName() &&
                    !Objects.isNull(candidateEducationEntity.getDegreeName()) && !candidateEducationEntity.getDegreeName().isEmpty()) {
                key = candidateEducationEntity.getDegreeName();

                if (!Objects.isNull(candidateEducationEntity.getIsYear()) && candidateEducationEntity.getIsYear() &&
                        !Objects.isNull(candidateEducationEntity.getYear()) && !candidateEducationEntity.getYear().isEmpty()) {
                    year = candidateEducationEntity.getYear();
                }
            }
            if (!Objects.isNull(candidateEducationEntity.getIsCollege()) && candidateEducationEntity.getIsCollege() &&
                    !Objects.isNull(candidateEducationEntity.getCollege()) && !candidateEducationEntity.getCollege().isEmpty()) {
                value = candidateEducationEntity.getCollege();
            }
            setKeyAndValue(key, value, year, cell, montserratFont);
        }

    }

    private void setContactDetails(Cell cell, ResumeDataDto resumeDataDto, PdfFont montserratFont) {

        Paragraph contactTitle = new Paragraph("Contact details");
        contactTitle.setTextAlignment(TextAlignment.LEFT);
        contactTitle.setFontSize(11);
        contactTitle.setBold();
        contactTitle.setFontColor(whiteRgb);
        contactTitle.setFont(montserratFont);
        contactTitle.setMarginTop(5);
        cell.add(contactTitle);

        setLine(cell, 0, 0);

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsEmail()) && resumeDataDto.getIsEmail() &&
                !Objects.isNull(resumeDataDto.getEmail()) && !resumeDataDto.getEmail().isEmpty()) {
            setKeyAndValue("Email", resumeDataDto.getEmail(), null, cell, montserratFont);
        }
        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsMobilePhone()) && resumeDataDto.getIsMobilePhone() &&
                !Objects.isNull(resumeDataDto.getMobilePhone()) && !resumeDataDto.getMobilePhone().isEmpty()) {
            setKeyAndValue("Phone", resumeDataDto.getMobilePhone(), null, cell, montserratFont);
        }
        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsCompanyName()) && resumeDataDto.getIsCompanyName() &&
                !Objects.isNull(resumeDataDto.getCompanyName()) && !resumeDataDto.getCompanyName().isEmpty()) {
            setKeyAndValue("Company", resumeDataDto.getCompanyName(), null, cell, montserratFont);
        }
        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsAddress()) && resumeDataDto.getIsAddress() &&
                !Objects.isNull(resumeDataDto.getAddress()) && !resumeDataDto.getAddress().isEmpty()) {
            setKeyAndValue("Address", resumeDataDto.getAddress(), null, cell, montserratFont);
        }
    }

    void setKeyAndValue(String key, String value, String year, Cell cell, PdfFont montserratFont) {

        List list = new List();
        list.setListSymbol("");

        Boolean isYear = false;

        if (!Objects.isNull(year) && !year.trim().isEmpty()) {
            ListItem yearTitle = new ListItem(year);
            yearTitle.setTextAlignment(TextAlignment.LEFT);
            yearTitle.setFontSize(8);
            yearTitle.setFontColor(whiteRgb);
            yearTitle.setFont(montserratFont);
            yearTitle.setMarginTop(5);
            list.add(yearTitle);
            isYear = true;
        }

        ListItem keyTitle = new ListItem(key);
        keyTitle.setTextAlignment(TextAlignment.LEFT);
        keyTitle.setFontSize(9);
        keyTitle.setBold();
        if (!isYear) {
            keyTitle.setMarginTop(5);
        }
        keyTitle.setFontColor(whiteRgb);
        keyTitle.setFont(montserratFont);
        list.add(keyTitle);


        if (!Objects.isNull(value) && !value.trim().isEmpty()) {
            ListItem valueTitle = new ListItem(value);
            valueTitle.setTextAlignment(TextAlignment.LEFT);
            valueTitle.setFontSize(8);
            valueTitle.setFontColor(whiteRgb);
            valueTitle.setFont(montserratFont);
            valueTitle.setMarginTop(-2);
            list.add(valueTitle);
        }


        Cell detailCell = new Cell();
        detailCell.add(list);
        detailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        cell.add(detailCell);
    }

    void setLine(Cell document, float right, float left) {
        Table headerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
        headerTable.setBorder(new SolidBorder(grayRgb2, 0.1f)); // Set border color and width
        headerTable.setMargins(0, right, 0, left);
        document.add(headerTable);
    }

    private void setCandidateImage(ResumeDataDto resumeDataDto, Cell cell) throws IOException {
        Image image = null;
        try {
            String imageUrl = "https://web.kakoo-software.com/kakoo-back-end/" + "company/" + 81 + "/downloadPhoto";
            image = new Image(ImageDataFactory.create(new URL(imageUrl)));

        } catch (Exception e) {
            ClassPathResource resource = new ClassPathResource("/static/logo_kakoo.png");
            image = new Image(ImageDataFactory.create(resource.getURL()));
        }
        image.setMargins(10, 5, 5, 5);
        image.scaleToFit(100f, 70f);
        image.setTextAlignment(TextAlignment.CENTER);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
        image.setBorderRadius(new BorderRadius(15));
        Cell imageCell = new Cell();
        imageCell.add(image);
        imageCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        cell.add(imageCell);
    }


}
