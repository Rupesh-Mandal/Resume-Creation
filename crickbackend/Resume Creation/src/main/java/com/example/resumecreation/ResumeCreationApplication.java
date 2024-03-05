package com.example.resumecreation;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.Document;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.jsoup.nodes.Element;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.jsoup.Jsoup;

@RestController
@SpringBootApplication
public class ResumeCreationApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ResumeCreationApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public ResponseEntity<byte[]> generatePdf() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Create a PdfWriter object to write to a file
            PdfWriter writer = new PdfWriter(baos);

            // Create a PdfDocument object to represent the PDF
            PdfDocument pdf = new PdfDocument(writer);

            // Create a Document object to add elements to the PDF
            Document document = new Document(pdf);

            DeviceRgb deviceRgb = new DeviceRgb(249, 190, 143);
            Color backgroundColor = deviceRgb; // Specify RGB values
            // Create an image object
            Image image = new Image(ImageDataFactory.create("/home/rupesh-mandal/IdeaProjects/CrickInformer/crickbackend/Resume Creation/src/main/resources/static/logo.png"));

            image.setMargins(5,5,5,5);
            // Set the image position to the top left corner
            image.scaleToFit(150f, 50f);
            document.add(image);


            Paragraph saulo = new Paragraph("SALOUA.");
            saulo.setMarginTop(10);
            saulo.setTextAlignment(TextAlignment.RIGHT);
            saulo.setFontSize(12);
            saulo.setFontColor(backgroundColor);
            document.add(saulo);



            // Create a headerParagraph with text
            Paragraph headerParagraph = new Paragraph("Consultant Support N3");
            headerParagraph.setTextAlignment(TextAlignment.CENTER);
            headerParagraph.setFontSize(20);
            headerParagraph.setBold();
            headerParagraph.setMarginTop(15);


            Paragraph headerParagraph2 = new Paragraph("5 ans d’expérience");
            headerParagraph2.setTextAlignment(TextAlignment.CENTER);
            headerParagraph2.setFontSize(13);
            headerParagraph2.setItalic();

            // Create a headerTable to add the headerParagraph with an outside border
            Table headerTable = new Table(UnitValue.createPercentArray(1)).useAllAvailableWidth();
            headerTable.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)); // Set border color and width
            headerTable.setPadding(5);
            headerTable.setMargin(10);
            // Combine the paragraphs into a single paragraph
            Paragraph combinedHeaderParagraph = new Paragraph()
                    .add(headerParagraph)
                    .add("\n") // Add a newline for separation
                    .add(headerParagraph2);
            combinedHeaderParagraph.setTextAlignment(TextAlignment.CENTER);
            headerTable.addCell(combinedHeaderParagraph);
            document.add(headerTable);

            Paragraph completenceParagraph = new Paragraph("COMPETENCES");
            completenceParagraph.setMarginTop(25);
            completenceParagraph.setTextAlignment(TextAlignment.CENTER);
            completenceParagraph.setFontSize(12);
            // Set background color for the paragraph
            completenceParagraph.setBackgroundColor(backgroundColor);
            document.add(completenceParagraph);


            // Create a list
            List list = new List();
            list.setMarginRight(10);
            list.setMarginLeft(10);
            list.setMarginTop(25);
            list.setPaddingLeft(10);
            list.setPaddingRight(10);
            // Add items to the list
            // Provide the path to your custom font file
            String fontPath = "static/fonts/montserrat_egular.ttf";
            PdfFont montserratFont = PdfFontFactory.createFont(fontPath);

            for (int i = 0; i < 20; i++) {
                var listItem = new ListItem("Système d’acceptation ; Télécollecte des transactions ; Pré-Acquisition");
                listItem.setPaddingLeft(10);
                listItem.setPaddingRight(10);
                listItem.setFont(montserratFont);
                list.add(listItem);
            }

            // Customize the list style
            list.setListSymbol("\u2022"); // Set the bullet point symbol
            list.setTextAlignment(TextAlignment.LEFT);

            // Add the list to the document
            document.add(list);


            Paragraph experiencesProfessionnellesParagraph = new Paragraph("EXPERIENCES PROFESSIONNELLES");
            experiencesProfessionnellesParagraph.setMarginTop(25);
            experiencesProfessionnellesParagraph.setTextAlignment(TextAlignment.CENTER);
            experiencesProfessionnellesParagraph.setFontSize(15);

            // Set background color for the paragraph
            experiencesProfessionnellesParagraph.setBackgroundColor(backgroundColor);
            document.add(experiencesProfessionnellesParagraph);

            for (int i = 0; i < 3; i++) {
                Paragraph experienceTtleParagraph = new Paragraph("Mars 2023 – Aujourd’hui : Payment Technical Support Analyst à Market Pay pour\n" +
                        "Carrefour");
                experienceTtleParagraph.setMarginTop(25);
                experienceTtleParagraph.setTextAlignment(TextAlignment.LEFT);
                experienceTtleParagraph.setFontSize(12);
                experienceTtleParagraph.setUnderline();
                experienceTtleParagraph.setBold();
                document.add(experienceTtleParagraph);


                Paragraph techParagraph = new Paragraph("Tâches:");
                techParagraph.setMarginTop(10);
                techParagraph.setTextAlignment(TextAlignment.LEFT);
                techParagraph.setFontSize(12);
                techParagraph.setFontColor(backgroundColor);
                document.add(techParagraph);


                // Create a list
                List skillList = new List();
                skillList.setMarginRight(10);
                skillList.setMarginLeft(10);
                skillList.setMarginTop(5);
                skillList.setPaddingLeft(10);
                skillList.setPaddingRight(10);

                for (int i2 = 0; i2 < 5; i2++) {
                    var listItem = new ListItem("Gestion et analyse des incidents et demandes de services dans le respect des SLA");
                    listItem.setPaddingLeft(10);
                    listItem.setPaddingRight(10);
                    listItem.setFont(montserratFont);
                    skillList.add(listItem);
                }

                // Customize the list style
                skillList.setListSymbol("\u2022"); // Set the bullet point symbol
                skillList.setTextAlignment(TextAlignment.LEFT);

                // Add the list to the document
                document.add(skillList);


            }


            Paragraph formationParagraph = new Paragraph("FORMATIONS");
            formationParagraph.setMarginTop(25);
            formationParagraph.setTextAlignment(TextAlignment.CENTER);
            formationParagraph.setFontSize(12);
            formationParagraph.setBackgroundColor(backgroundColor);
            document.add(formationParagraph);


            // Create a headerTable to add the headerParagraph with an outside border
            Table formationTable = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            formationTable.setBorder(new SolidBorder(new DeviceRgb(0, 0, 0), 1)); // Set border color and width



            for (int i = 0; i < 4; i++) {
                Paragraph key = new Paragraph("5 ans d’expérience");
                Paragraph value = new Paragraph("5 ans d’expérience");
                key.setTextAlignment(TextAlignment.CENTER);
                value.setTextAlignment(TextAlignment.CENTER);

                // Add cells to the table
                formationTable.addCell(key);
                formationTable.addCell(value);
            }


            // Add the table to the document
            document.add(formationTable);

            Paragraph languageParagraph = new Paragraph("Langues: Anglais (Professionnel) Français (Courant)");
            languageParagraph.setMarginTop(0);
            languageParagraph.setTextAlignment(TextAlignment.CENTER);
            languageParagraph.setFontSize(12);
            languageParagraph.setBackgroundColor(backgroundColor);
            document.add(languageParagraph);




            Paragraph companyName = new Paragraph("MONEYCORE FRANCE");
            companyName.setMarginTop(20);
            companyName.setTextAlignment(TextAlignment.CENTER);
            companyName.setFontSize(10);
            companyName.setFontColor(backgroundColor);
            companyName.setBold();
            document.add(companyName);


            Paragraph companyDetails = new Paragraph("37 Rue Adam Ledoux - 92400 Courbevoie France – Email : contact@moneycore.fr - Tél. : 01.84.20.25.15 Capital " +
                    "100 000 € - Siret : 804 642 239 000 37 - RCS : Nanterre – TVA : FR55804642239 - Code APE : 6202A");
            companyDetails.setMarginTop(2);
            companyDetails.setTextAlignment(TextAlignment.LEFT);
            companyDetails.setFontSize(8);
            document.add(companyDetails);

            document.close();

            // Build the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "example.pdf");

            return ResponseEntity.ok().headers(headers).body(baos.toByteArray());
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }


    @Override
    public void run(String... args) throws Exception {
        String rawData = "-, LA REDOUTE, Paiement & Fraude\n" +
                "Chef de Projet Monétique\n" +
                "Mise en conformité DSP2 (3DSv2 et choix de la marque)\n" +
                "O Participation active aux ateliers de l'OSMP (Observatoire de la Sécurité des Moyens de paiement)\n" +
                "Gestion de l'authentification renforcée DSP2\n" +
                "O\n" +
                "O Coordination avec les métiers, les équipes IT et les partenaires externes (PSP, banques, schemes)\n" +
                "Réalisation d'études (analyse d'impact, études préalables, d'opportunité, de faisabilité)\n" +
                "O\n" +
                "O\n" +
                "Rédaction de cahiers des charges et spécifications fonctionnelles\n" +
                "O\n" +
                "O\n" +
                "Intégration du nouveau PSP/PAT Stripe\n" +
                "O Participation active aux études de cadrage en amont : ateliers, définition des règles de gestion et\n" +
                "maîtrise des processus métiers\n" +
                "O Analyse d'impact des besoins métiers existants\n" +
                "O Participation aux phases de test d'intégration, de formation et de recette utilisateur\n" +
                "(définition de la stratégie et du plan de test, déroulement et suivi des cas de tests)\n" +
                "Définition de la stratégie de recette et pilotage de la réalisation\n" +
                "Gestion du périmètre applicatif (maintenance, incidents, solutions)\n" +
                "Intégration d'une nouvelle Banque Acquéreur\n" +
                "O Participation active aux études de cadrage en amont - Analyse d'impact\n" +
                "O Participation aux phases de test d'intégration, de formation et de recette utilisateur\n" +
                "Dans le cadre de l'équipe Monétique, gestion des diverses évolutions\n" +
                "O Proposition de nouveaux moyens de paiement sur les sites e-commerce de la Redoute\n" +
                "O Amélioration du taux de conversion\n" +
                "O Optimisation des outils Data pour le suivi des KPIs et des incidents opérationnels\n" +
                "O\n" +
                "Suivi du bon déroulement des ventes en ligne :\n" +
                "✓ Gestion de la sécurité de paiement\n" +
                "Qualité du service et SAV\n" +
                "Pour chacune des évolutions\n" +
                "O\n" +
                "Etude et analyse des demandes\n" +
                "Suivi des livraisons et des travaux de la maîtrise d'œuvre\n" +
                "Pilotage des contributeurs\n" +
                "O Animation des comités projets et de suivi MOA-MOE\n" +
                "oo\n" +
                "✓ Elaboration des stratégies web de l'entreprise.\n" +
                "✓ Pilotage des prestataires (Monext, Ingenico, Amex, Limonetik/Thunes...)";

        // Use Jsoup to parse the raw string
        org.jsoup.nodes.Document document = Jsoup.parse(rawData);

        // Extract text content from the parsed document
        String formattedText = document.text();

        // Print the formatted text
        System.out.println(formattedText);

    }

    private static String convertToHtml(String textData) {
        // Split text into lines
        String[] lines = textData.split("\\n");

        // Create an HTML document
        org.jsoup.nodes.Document document = Jsoup.parse("<html><body></body></html>");
        org.jsoup.nodes.Element body = document.body();

        // Iterate through lines and append them as paragraphs to the body
        for (String line : lines) {
            body.append("<p>").append(line).append("</p>");
        }

        // Get the HTML content
        return document.html();
    }
    // Helper method to convert raw data to HTML

}
