package com.example.resumecreation;

import com.itextpdf.html2pdf.HtmlConverter;
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
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

@RequiredArgsConstructor
@RestController
public class Controller {
    String fontPath = "static/fonts/montserrat_egular.ttf";

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
        ResponseEntity<ResumeDataDto> responseEntity = restTemplate.getForEntity("https://web.kakoo-software.com/kakoo-back-end/api/v1/pipeline/get-candidate-resume-for-job/377/candidate-id/330", ResumeDataDto.class);
        ResumeDataDto resumeDataDto = responseEntity.getBody();
        String outputDateFormat = "MMM dd, yyyy";

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

        if (!Objects.isNull(resumeDataDto.getUseCompanyLogo()) && resumeDataDto.getUseCompanyLogo()) {
            try {

                String imageUrl = "http://157.230.30.255:8080/kakoo-back-end/company/10956/downloadPhoto";

                // Create an Image object from the URL
                Image image = new Image(ImageDataFactory.create(new URL(imageUrl)));

//                    Image image = new Image(ImageDataFactory.create("/home/rupesh-mandal/IdeaProjects/CrickInformer/crickbackend/Resume Creation/src/main/resources/static/logo.png"));
                image.setMargins(5, 5, 5, 5);
                image.scaleToFit(150f, 50f);
                Cell imageCell = new Cell();
                imageCell.add(image);
                imageCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
                topTable.addCell(imageCell);
            } catch (Exception e) {
                topTable.addCell(new Cell().setBorder(Border.NO_BORDER));
            }
        } else {
            topTable.addCell(new Cell().setBorder(Border.NO_BORDER));
        }

        List companyDetailList = new List();
        companyDetailList.setListSymbol(" ");

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsCompanyName()) && resumeDataDto.getIsCompanyName() &&
                !Objects.isNull(resumeDataDto.getCompanyName()) && !resumeDataDto.getCompanyName().isEmpty()) {
            ListItem companyNameListItem = new ListItem(resumeDataDto.getCompanyName());
            companyNameListItem.setBold();
            companyNameListItem.setTextAlignment(TextAlignment.RIGHT);
            companyNameListItem.setFontSize(18);
            companyNameListItem.setFont(montserratFont);
            companyDetailList.add(companyNameListItem);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsAddress()) && resumeDataDto.getIsAddress() &&
                !Objects.isNull(resumeDataDto.getAddress()) && !resumeDataDto.getAddress().isEmpty()) {
            ListItem addressListItem = new ListItem(resumeDataDto.getAddress());
            addressListItem.setTextAlignment(TextAlignment.RIGHT);
            addressListItem.setFontSize(10);
            addressListItem.setFont(montserratFont);
            companyDetailList.add(addressListItem);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsEmail()) && resumeDataDto.getIsEmail() &&
                !Objects.isNull(resumeDataDto.getEmail()) && !resumeDataDto.getEmail().isEmpty()) {
            ListItem emailListItem = new ListItem(resumeDataDto.getEmail());
            emailListItem.setTextAlignment(TextAlignment.RIGHT);
            emailListItem.setFontSize(10);
            emailListItem.setFont(montserratFont);
            emailListItem.setUnderline();
            emailListItem.setFontColor(blueColor);
            companyDetailList.add(emailListItem);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsMobilePhone()) && resumeDataDto.getIsMobilePhone() &&
                !Objects.isNull(resumeDataDto.getMobilePhone()) && !resumeDataDto.getMobilePhone().isEmpty()) {
            ListItem phomeListItem = new ListItem(resumeDataDto.getMobilePhone());
            phomeListItem.setTextAlignment(TextAlignment.RIGHT);
            phomeListItem.setFontSize(10);
            phomeListItem.setFont(montserratFont);
            companyDetailList.add(phomeListItem);
        }


        Cell companyDetailCell = new Cell();
        companyDetailCell.add(companyDetailList);
        companyDetailCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
        topTable.addCell(companyDetailCell);
        document.add(topTable);

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isJobTitle) && resumeDataDto.isJobTitle &&
                !Objects.isNull(resumeDataDto.jobTitle) && !resumeDataDto.jobTitle.isEmpty()) {
            Paragraph profileParagraph = new Paragraph(resumeDataDto.jobTitle);
            profileParagraph.setTextAlignment(TextAlignment.LEFT);
            profileParagraph.setBold();
            profileParagraph.setFontSize(15);
            profileParagraph.setFont(montserratFont);
            profileParagraph.setPaddingLeft(5);
            profileParagraph.setPaddingRight(5);
            document.add(profileParagraph);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.getIsFullName()) && resumeDataDto.getIsFullName() &&
                !Objects.isNull(resumeDataDto.getFullName()) && !resumeDataDto.getFullName().isEmpty()) {
            Paragraph profileParagraph = new Paragraph(resumeDataDto.getFullName());
            profileParagraph.setTextAlignment(TextAlignment.LEFT);
            profileParagraph.setFontSize(14);
            profileParagraph.setFont(montserratFont);
            profileParagraph.setMarginTop(-8);
            profileParagraph.setPaddingLeft(5);
            profileParagraph.setPaddingRight(5);
            document.add(profileParagraph);
        }


        Paragraph candidateDataParagraph = new Paragraph("Candidate -#" + 122 + " , Job - #" + 234);
        candidateDataParagraph.setTextAlignment(TextAlignment.LEFT);
        candidateDataParagraph.setFontSize(10);
        candidateDataParagraph.setFont(montserratFont);
        candidateDataParagraph.setMarginTop(-8);
        candidateDataParagraph.setPaddingLeft(5);
        candidateDataParagraph.setPaddingRight(5);
        document.add(candidateDataParagraph);


        Table headerTable = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
        headerTable.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)); // Set border color and width
        headerTable.setPadding(5);
        headerTable.setMargin(10);

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isTotalExperience) && resumeDataDto.isTotalExperience &&
                !Objects.isNull(resumeDataDto.totalExperience)) {
            Cell experienceCell = new Cell();
            experienceCell.add(new Paragraph("Experience").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            experienceCell.add(new Paragraph(resumeDataDto.totalExperience + " Years").setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            experienceCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(experienceCell);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isAvailability) && resumeDataDto.isAvailability &&
                !Objects.isNull(resumeDataDto.availability) && !resumeDataDto.availability.isEmpty()) {
            Cell availabilityCell = new Cell();
            availabilityCell.add(new Paragraph("Availability").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            availabilityCell.add(new Paragraph(resumeDataDto.availability).setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            availabilityCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(availabilityCell);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isEmploymentTypeEntity) && resumeDataDto.isEmploymentTypeEntity &&
                !Objects.isNull(resumeDataDto.getEmploymentTypeEntity()) &&
                !Objects.isNull(resumeDataDto.getEmploymentTypeEntity().name) && !resumeDataDto.getEmploymentTypeEntity().name.isEmpty()) {
            Cell employementTypeCell = new Cell();
            employementTypeCell.add(new Paragraph("Employment Type").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            employementTypeCell.add(new Paragraph(resumeDataDto.getEmploymentTypeEntity().name).setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            employementTypeCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(employementTypeCell);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isDesiredWorkType) && resumeDataDto.isDesiredWorkType &&
                !Objects.isNull(resumeDataDto.desiredWorkType) && !resumeDataDto.desiredWorkType.isEmpty()) {
            Cell workTypeCell = new Cell();
            workTypeCell.add(new Paragraph("Work Type").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            workTypeCell.add(new Paragraph(resumeDataDto.desiredWorkType).setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            workTypeCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(workTypeCell);
        }


        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isNationality) && resumeDataDto.isNationality &&
                !Objects.isNull(resumeDataDto.nationality) && !resumeDataDto.nationality.isEmpty()) {
            Cell nationalityCell = new Cell();
            nationalityCell.add(new Paragraph("Nationality").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            nationalityCell.add(new Paragraph(resumeDataDto.nationality).setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            nationalityCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(nationalityCell);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isLocation) && resumeDataDto.isLocation &&
                !Objects.isNull(resumeDataDto.location) && !resumeDataDto.location.isEmpty()) {
            Cell locationCell = new Cell();
            locationCell.add(new Paragraph("Location").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            locationCell.add(new Paragraph(resumeDataDto.location).setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            locationCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(locationCell);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isScoreV) && resumeDataDto.isScoreV &&
                !Objects.isNull(resumeDataDto.getScoreV())) {
            Cell assessmentScoreCell = new Cell();
            assessmentScoreCell.add(new Paragraph("Assessment Score").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            assessmentScoreCell.add(new Paragraph(resumeDataDto.getScoreV() + "%").setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            assessmentScoreCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(assessmentScoreCell);
        }

        if (!Objects.isNull(resumeDataDto.isPersonalDetails) && resumeDataDto.isPersonalDetails &&
                !Objects.isNull(resumeDataDto.isEvaluation) && resumeDataDto.isEvaluation &&
                !Objects.isNull(resumeDataDto.getEvaluation()) && resumeDataDto.getEvaluation()>0) {
            Cell assessmentScoreCell = new Cell();
            String s="";
            for (Integer i = 0; i < resumeDataDto.getEvaluation(); i++) {
               s=s+"*";
            }
            assessmentScoreCell.add(new Paragraph("Evaluation").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
            assessmentScoreCell.add(new Paragraph(s)
                    .setFontSize(20).setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
            assessmentScoreCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
            headerTable.addCell(assessmentScoreCell);
        }


//            Cell evaluationCell = new Cell();
//            evaluationCell.add(new Paragraph("Evaluation").setFont(montserratFont).setTextAlignment(TextAlignment.CENTER));
//            evaluationCell.add(new Paragraph("Yes").setFont(montserratFont).setFontColor(greenColor).setTextAlignment(TextAlignment.CENTER));
//            evaluationCell.setBorder(Border.NO_BORDER); // Remove borders for this specific cell
//            headerTable.addCell(evaluationCell);

        document.add(headerTable);

        if (!Objects.isNull(resumeDataDto.isAboutContent) && resumeDataDto.isAboutContent &&
                !Objects.isNull(resumeDataDto.aboutContent) && !resumeDataDto.aboutContent.isEmpty()) {
            Paragraph summeryParagraph = new Paragraph("Professional Summary");
            summeryParagraph.setTextAlignment(TextAlignment.LEFT);
            summeryParagraph.setBold();
            summeryParagraph.setFontSize(12);
            summeryParagraph.setFont(montserratFont);
            summeryParagraph.setPaddingLeft(5);
            summeryParagraph.setPaddingRight(5);
            summeryParagraph.setUnderline();
            document.add(summeryParagraph);

            org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(resumeDataDto.aboutContent);
            processHtmlElementsForSummery(jsoupDocument.body(), document);
//
//            Paragraph summeryDtailsParagraph = new Paragraph(Jsoup.parse(resumeDataDto.aboutContent).text());
//            summeryDtailsParagraph.setTextAlignment(TextAlignment.LEFT);
//            summeryDtailsParagraph.setFontSize(10);
//            summeryDtailsParagraph.setFont(montserratFont);
//            summeryDtailsParagraph.setMarginTop(-2);
//            summeryDtailsParagraph.setPaddingLeft(5);
//            summeryDtailsParagraph.setPaddingRight(5);
//            document.add(summeryDtailsParagraph);
        }

        if (!Objects.isNull(resumeDataDto.isSkillListEntities) && resumeDataDto.isSkillListEntities &&
                !Objects.isNull(resumeDataDto.getSkillListEntities()) && !resumeDataDto.getSkillListEntities().isEmpty()) {
            Paragraph skillParagraph = new Paragraph("Skills");
            skillParagraph.setTextAlignment(TextAlignment.LEFT);
            skillParagraph.setBold();
            skillParagraph.setFontSize(12);
            skillParagraph.setFont(montserratFont);
            skillParagraph.setPaddingLeft(5);
            skillParagraph.setPaddingRight(5);
            skillParagraph.setUnderline();
            document.add(skillParagraph);

            Paragraph skillList = new Paragraph();

            for (ResumeDataDto.CandidateSkillEntity skillListEntity : resumeDataDto.getSkillListEntities()) {
                String name = Jsoup.parse(skillListEntity.name).text();
                Paragraph skillItem = new Paragraph(name);
                skillItem.setFontSize(10);
                skillItem.setMarginLeft(5);
                skillItem.setMarginRight(5);
                skillItem.setPaddingRight(5); // Set padding
                skillItem.setPaddingLeft(5); // Set padding
                skillItem.setBorderRadius(new BorderRadius(10));
                skillItem.setFont(montserratFont);
                if (!Objects.isNull(skillListEntity.evaluation)){
                    if (skillListEntity.evaluation.trim().equalsIgnoreCase("1")){
                        skillItem.setBackgroundColor(new DeviceRgb(166,20,37));// Set background color
                        skillItem.setFontColor(new DeviceRgb(255,255,255));
                    } else if (skillListEntity.evaluation.trim().equalsIgnoreCase("2")) {
                        skillItem.setBackgroundColor(new DeviceRgb(12,42,138));// Set background color
                        skillItem.setFontColor(new DeviceRgb(255,255,255));
                    } else if (skillListEntity.evaluation.trim().equalsIgnoreCase("3")) {
                        skillItem.setBackgroundColor(new DeviceRgb(47,196,24));// Set background color
                        skillItem.setFontColor(new DeviceRgb(255,255,255));
                    } else if (skillListEntity.evaluation.trim().equalsIgnoreCase("4")) {
                        skillItem.setBackgroundColor(new DeviceRgb(18,99,5));// Set background color
                        skillItem.setFontColor(new DeviceRgb(255,255,255));
                    }else {
                        skillItem.setBackgroundColor(new DeviceRgb(248,248,248));// Set background color
                    }
                }else {
                    skillItem.setBackgroundColor(new DeviceRgb(248,248,248));// Set background color
                }


                skillList.add(skillItem);
            }
            document.add(skillList);
        }
        if (!Objects.isNull(resumeDataDto.isOtherSkill) && resumeDataDto.isOtherSkill &&
                !Objects.isNull(resumeDataDto.otherSkill) && !resumeDataDto.otherSkill.isEmpty()) {
            org.jsoup.nodes.Document jsoupDocument = Jsoup.parse(resumeDataDto.otherSkill);
            processHtmlElementsForSkill(jsoupDocument.body(), document);
        }



        if (!Objects.isNull(resumeDataDto.getIsWorkExperienceEntities()) && resumeDataDto.getIsWorkExperienceEntities()) {
            if (!Objects.isNull(resumeDataDto.getWorkExperienceEntities()) && !resumeDataDto.getWorkExperienceEntities().isEmpty()) {
                Paragraph employmentHistoryParagraph = new Paragraph("Employment History");
                employmentHistoryParagraph.setTextAlignment(TextAlignment.LEFT);
                employmentHistoryParagraph.setBold();
                employmentHistoryParagraph.setFontSize(13);
                employmentHistoryParagraph.setFont(montserratFont);
                employmentHistoryParagraph.setPaddingLeft(5);
                employmentHistoryParagraph.setPaddingRight(5);
                employmentHistoryParagraph.setMarginBottom(-5);
                employmentHistoryParagraph.setUnderline();
                document.add(employmentHistoryParagraph);

                for (ResumeDataDto.WorkExperienceEntity workExperienceEntity : resumeDataDto.getWorkExperienceEntities()) {
                    Cell marginCell = new Cell();
                    marginCell.setMarginTop(4);
                    document.add(marginCell);

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
                        employmentHistoryTitleParagraph.setFontSize(11);
                        employmentHistoryTitleParagraph.setFont(montserratFont);
                        employmentHistoryTitleParagraph.setPaddingLeft(5);
                        employmentHistoryTitleParagraph.setPaddingRight(5);
                        document.add(employmentHistoryTitleParagraph);
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
                    titleParagraph.setFontSize(10);
                    titleParagraph.setFont(montserratFont);
                    titleParagraph.setPaddingLeft(5);
                    titleParagraph.setPaddingRight(5);
                    titleParagraph.setMarginTop(-8);
                    document.add(titleParagraph);

                    if (!Objects.isNull(workExperienceEntity.isDescription) && workExperienceEntity.isDescription &&
                            !Objects.isNull(workExperienceEntity.description) && !workExperienceEntity.description.isEmpty()) {
                        Paragraph descriptionParagraph = new Paragraph(Jsoup.parse(workExperienceEntity.description).text());
                        descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                        descriptionParagraph.setFontSize(10);
                        descriptionParagraph.setFont(montserratFont);
                        descriptionParagraph.setPaddingLeft(5);
                        descriptionParagraph.setPaddingRight(5);
                        descriptionParagraph.setMarginTop(-8);
                        document.add(descriptionParagraph);
                    }


                }
            }
        }

        if (!Objects.isNull(resumeDataDto.getIsProjectPortfolioEntities()) && resumeDataDto.getIsProjectPortfolioEntities()) {
            if (!Objects.isNull(resumeDataDto.getProjectPortfolioEntities()) && !resumeDataDto.getProjectPortfolioEntities().isEmpty()) {
                Paragraph recentProjectsParagraph = new Paragraph("Recent Projects");
                recentProjectsParagraph.setTextAlignment(TextAlignment.LEFT);
                recentProjectsParagraph.setBold();
                recentProjectsParagraph.setFontSize(13);
                recentProjectsParagraph.setFont(montserratFont);
                recentProjectsParagraph.setPaddingLeft(5);
                recentProjectsParagraph.setPaddingRight(5);
                recentProjectsParagraph.setMarginBottom(-5);
                recentProjectsParagraph.setUnderline();
                document.add(recentProjectsParagraph);

                for (ResumeDataDto.ProjectPortfolioEntity projectPortfolioEntity : resumeDataDto.getProjectPortfolioEntities()) {
                    Cell marginCell = new Cell();
                    marginCell.setMarginTop(4);
                    document.add(marginCell);

                    if (!Objects.isNull(projectPortfolioEntity.isProjectTitle) && projectPortfolioEntity.isProjectTitle &&
                            !Objects.isNull(projectPortfolioEntity.projectTitle) && !projectPortfolioEntity.projectTitle.isEmpty()) {
                        Paragraph titleParagraph = new Paragraph(projectPortfolioEntity.projectTitle);
                        titleParagraph.setTextAlignment(TextAlignment.LEFT);
                        titleParagraph.setBold();
                        titleParagraph.setFontSize(10);
                        titleParagraph.setFont(montserratFont);
                        titleParagraph.setPaddingLeft(5);
                        titleParagraph.setPaddingRight(5);
                        document.add(titleParagraph);
                    }
                    if (!Objects.isNull(projectPortfolioEntity.isProjectDescription) && projectPortfolioEntity.isProjectDescription &&
                            !Objects.isNull(projectPortfolioEntity.projectDescription) && !projectPortfolioEntity.projectDescription.isEmpty()) {
                        Paragraph descriptionParagraph = new Paragraph(Jsoup.parse(projectPortfolioEntity.projectDescription).text());
                        descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                        descriptionParagraph.setFontSize(10);
                        descriptionParagraph.setFont(montserratFont);
                        descriptionParagraph.setPaddingLeft(5);
                        descriptionParagraph.setPaddingRight(5);
                        descriptionParagraph.setMarginTop(-8);
                        document.add(descriptionParagraph);

                    }
                }
            }
        }


        if (!Objects.isNull(resumeDataDto.getIsCandidateEducationEntities()) && resumeDataDto.getIsCandidateEducationEntities()) {
            if (!Objects.isNull(resumeDataDto.getCandidateEducationEntities()) && !resumeDataDto.getCandidateEducationEntities().isEmpty()) {
                Paragraph educationParagraph = new Paragraph("Education");
                educationParagraph.setTextAlignment(TextAlignment.LEFT);
                educationParagraph.setBold();
                educationParagraph.setFontSize(13);
                educationParagraph.setFont(montserratFont);
                educationParagraph.setPaddingLeft(5);
                educationParagraph.setPaddingRight(5);
                educationParagraph.setMarginBottom(-5);
                educationParagraph.setUnderline();
                document.add(educationParagraph);

                for (ResumeDataDto.CandidateEducationEntity candidateEducationEntity : resumeDataDto.getCandidateEducationEntities()) {
                    Cell marginCell = new Cell();
                    marginCell.setMarginTop(4);
                    document.add(marginCell);

                    if (!Objects.isNull(candidateEducationEntity.getIsDegreeName()) && candidateEducationEntity.getIsDegreeName() &&
                            !Objects.isNull(candidateEducationEntity.getDegreeName()) && !candidateEducationEntity.getDegreeName().isEmpty()) {
                        String title = "";
                        title = candidateEducationEntity.getDegreeName();
                        Paragraph titleParagraph = new Paragraph(title);
                        titleParagraph.setTextAlignment(TextAlignment.LEFT);
                        titleParagraph.setBold();
                        titleParagraph.setFontSize(10);
                        titleParagraph.setFont(montserratFont);
                        titleParagraph.setPaddingLeft(5);
                        titleParagraph.setPaddingRight(5);

                        Paragraph combinedParagraph = new Paragraph();
                        combinedParagraph.add(titleParagraph);

                        if (!Objects.isNull(candidateEducationEntity.getIsYear()) && candidateEducationEntity.getIsYear() &&
                                !Objects.isNull(candidateEducationEntity.getYear()) && !candidateEducationEntity.getYear().isEmpty()) {
                            Paragraph paragraph2 = new Paragraph(" - " + candidateEducationEntity.getYear());
                            paragraph2.setFont(montserratFont);
                            paragraph2.setItalic();
                            paragraph2.setFontSize(10);
                            combinedParagraph.add(paragraph2);
                        }
                        document.add(combinedParagraph);
                    }
                    if (!Objects.isNull(candidateEducationEntity.getIsCollege()) && candidateEducationEntity.getIsCollege() &&
                            !Objects.isNull(candidateEducationEntity.getCollege()) && !candidateEducationEntity.getCollege().isEmpty()) {
                        Paragraph descriptionParagraph = new Paragraph(candidateEducationEntity.getCollege());
                        descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                        descriptionParagraph.setFontSize(10);
                        descriptionParagraph.setFont(montserratFont);
                        descriptionParagraph.setPaddingLeft(5);
                        descriptionParagraph.setPaddingRight(5);
                        descriptionParagraph.setMarginTop(-8);
                        document.add(descriptionParagraph);

                    }
                }
            }
        }


        if (!Objects.isNull(resumeDataDto.getIsCandidateCertificateEntities()) && resumeDataDto.getIsCandidateCertificateEntities()) {
            if (!Objects.isNull(resumeDataDto.getCandidateCertificateEntities()) && !resumeDataDto.getCandidateCertificateEntities().isEmpty()) {
                Paragraph certificationParagraph = new Paragraph("Certification");
                certificationParagraph.setTextAlignment(TextAlignment.LEFT);
                certificationParagraph.setBold();
                certificationParagraph.setFontSize(13);
                certificationParagraph.setFont(montserratFont);
                certificationParagraph.setPaddingLeft(5);
                certificationParagraph.setPaddingRight(5);
                certificationParagraph.setMarginBottom(-5);
                certificationParagraph.setUnderline();
                document.add(certificationParagraph);

                for (ResumeDataDto.CandidateCertificateEntity candidateCertificateEntity : resumeDataDto.getCandidateCertificateEntities()) {
                    Cell marginCell = new Cell();
                    marginCell.setMarginTop(4);
                    document.add(marginCell);

                    if (!Objects.isNull(candidateCertificateEntity.getIsName()) && candidateCertificateEntity.getIsName() &&
                            !Objects.isNull(candidateCertificateEntity.getName()) && !candidateCertificateEntity.getName().isEmpty()) {
                        Paragraph titleParagraph = new Paragraph(candidateCertificateEntity.getName());
                        titleParagraph.setTextAlignment(TextAlignment.LEFT);
                        titleParagraph.setBold();
                        titleParagraph.setFontSize(10);
                        titleParagraph.setFont(montserratFont);
                        titleParagraph.setPaddingLeft(5);
                        titleParagraph.setPaddingRight(5);
                        document.add(titleParagraph);
                    }
                    if (!Objects.isNull(candidateCertificateEntity.getIsInstitution()) && candidateCertificateEntity.getIsInstitution()) {
                        String title = "";
                        title = candidateCertificateEntity.getInstitution();
                        if (!Objects.isNull(candidateCertificateEntity.getIsYear()) && candidateCertificateEntity.getIsYear() &&
                                !Objects.isNull(candidateCertificateEntity.getYear()) && !candidateCertificateEntity.getYear().isEmpty()) {
                            title = title + ", " + candidateCertificateEntity.getYear();
                        }
                        Paragraph descriptionParagraph = new Paragraph(title);
                        descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                        descriptionParagraph.setFontSize(10);
                        descriptionParagraph.setFont(montserratFont);
                        descriptionParagraph.setPaddingLeft(5);
                        descriptionParagraph.setPaddingRight(5);
                        descriptionParagraph.setMarginTop(-8);
                        document.add(descriptionParagraph);

                    }
                }
            }
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


                    Paragraph languageParagraph = new Paragraph("Language");
                    languageParagraph.setTextAlignment(TextAlignment.LEFT);
                    languageParagraph.setBold();
                    languageParagraph.setFontSize(13);
                    languageParagraph.setFont(montserratFont);
                    languageParagraph.setPaddingLeft(5);
                    languageParagraph.setPaddingRight(5);
                    languageParagraph.setUnderline();
                    document.add(languageParagraph);

                    for (ResumeDataDto.LanguageEntity languageEntity : resumeDataDto.languageEntities) {
                        if (!Objects.isNull(languageEntity.getIsName()) && languageEntity.getIsName()) {
                            String title = languageEntity.getName();

                            Paragraph titleParagraph = new Paragraph(title);
                            titleParagraph.setTextAlignment(TextAlignment.LEFT);
                            titleParagraph.setBold();
                            titleParagraph.setFontSize(10);
                            titleParagraph.setFont(montserratFont);
                            titleParagraph.setPaddingLeft(5);
                            titleParagraph.setPaddingRight(5);

                            Paragraph combinedParagraph = new Paragraph();
                            combinedParagraph.add(titleParagraph);
                            combinedParagraph.setMarginTop(-5);

                            if (!Objects.isNull(languageEntity.getIsProficiency()) && languageEntity.getIsProficiency() &&
                                    !Objects.isNull(languageEntity.getProficiency()) && !languageEntity.getProficiency().isEmpty()) {
                                Paragraph paragraph2 = new Paragraph(" - " + languageEntity.getProficiency());
                                paragraph2.setFont(montserratFont);
                                paragraph2.setItalic();
                                paragraph2.setFontSize(10);
                                combinedParagraph.add(paragraph2);
                            }
                            document.add(combinedParagraph);
                        }
                    }
                }
            }
        }


        if (!Objects.isNull(resumeDataDto.getIsCustomDetails()) && resumeDataDto.getIsCustomDetails()) {
            if (!Objects.isNull(resumeDataDto.getCustomDetails()) && !resumeDataDto.getCustomDetails().isEmpty()) {
                Paragraph otherDetailsParagraph = new Paragraph("Other details");
                otherDetailsParagraph.setTextAlignment(TextAlignment.LEFT);
                otherDetailsParagraph.setBold();
                otherDetailsParagraph.setFontSize(13);
                otherDetailsParagraph.setFont(montserratFont);
                otherDetailsParagraph.setPaddingLeft(5);
                otherDetailsParagraph.setPaddingRight(5);
                otherDetailsParagraph.setUnderline();
                otherDetailsParagraph.setMarginBottom(-3);
                document.add(otherDetailsParagraph);

                for (ResumeDataDto.CustomDetails customDetail : resumeDataDto.getCustomDetails()) {
                    if (!Objects.isNull(customDetail.fieldName) && !customDetail.fieldName.isEmpty()) {
                        String title = customDetail.fieldName;

                        Paragraph titleParagraph = new Paragraph(title);
                        titleParagraph.setTextAlignment(TextAlignment.LEFT);
                        titleParagraph.setBold();
                        titleParagraph.setFontSize(10);
                        titleParagraph.setFont(montserratFont);
                        titleParagraph.setPaddingLeft(5);
                        titleParagraph.setPaddingRight(5);

                        Paragraph combinedParagraph = new Paragraph();
                        combinedParagraph.add(titleParagraph);
                        combinedParagraph.setMarginTop(-3);

                        if (!Objects.isNull(customDetail.activityName) && !customDetail.activityName.isEmpty()) {
                            Paragraph paragraph2 = new Paragraph(" - " + customDetail.activityName);
                            paragraph2.setFont(montserratFont);
                            paragraph2.setItalic();
                            paragraph2.setFontSize(10);
                            combinedParagraph.add(paragraph2);
                        }
                        document.add(combinedParagraph);

                    }
                    if (!Objects.isNull(customDetail.getDescription()) && !customDetail.getDescription().isEmpty()) {
                        Paragraph descriptionParagraph = new Paragraph(Jsoup.parse(customDetail.getDescription()).text());
                        descriptionParagraph.setTextAlignment(TextAlignment.LEFT);
                        descriptionParagraph.setFontSize(10);
                        descriptionParagraph.setFont(montserratFont);
                        descriptionParagraph.setPaddingLeft(5);
                        descriptionParagraph.setPaddingRight(5);
                        descriptionParagraph.setMarginTop(-8);
                        document.add(descriptionParagraph);

                    }
                }
            }
        }
        document.close();
        return baos;
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


    private void processHtmlElementsForSkill(Element element, Document pdfDocument) throws IOException {
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
                summeryDtailsParagraph.setFontSize(10);
                summeryDtailsParagraph.setFont(montserratFont);
                summeryDtailsParagraph.setMarginTop(-2);
                summeryDtailsParagraph.setPaddingLeft(5);
                summeryDtailsParagraph.setPaddingRight(5);
                pdfDocument.add(summeryDtailsParagraph); // Use bullet point for list items
            }
        }
    }


}
