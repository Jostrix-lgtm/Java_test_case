package com.mycompany.airline_system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

public class TicketBooking {

    private static int lastBusinessClassId;
    private static int lastEconomyClassId;

    public static void main(String args[]) throws IOException, IllegalStateException {
        loadLastAssignedIds();

        String businessFolder = "./extractedData/business"; // Folder containing business class text files
        String economyFolder = "./extractedData/economy"; // Folder containing economy class text files
        String businesext = "./Media/business"; 
        String economyext = "./Media/economy";
        
        
        processFolder(businessFolder, "business");
        processFolder(economyFolder, "economy");

        saveLastAssignedIds();
        
        deleteFilesInFolder(businessFolder);
        deleteFilesInFolder(economyFolder);
        deleteFilesInFolder(businesext);
        deleteFilesInFolder(economyext);
        Receipt.main(args);
//        deleteFilesInFolder(ticketfolder);
    }

    private static void loadLastAssignedIds() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./Media/lastAssignedIds.txt"));
            lastBusinessClassId = Integer.parseInt(reader.readLine());
            lastEconomyClassId = Integer.parseInt(reader.readLine());
            reader.close();
        } catch (IOException e) {
            // File doesn't exist or cannot be read, set default IDs
            lastBusinessClassId = 9000000; // Initial ID for business class
            lastEconomyClassId = 2000000; // Initial ID for economy class
        }
    }

    private static void saveLastAssignedIds() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./Media/lastAssignedIds.txt"));
            writer.write(String.valueOf(lastBusinessClassId));
            writer.newLine();
            writer.write(String.valueOf(lastEconomyClassId));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processFolder(String folderPath, String ticketClass) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    processTextFile(file, ticketClass);
                }
            }
        }
    }

    private static void processTextFile(File inputFile, String ticketClass) throws IOException {
        // Loading an existing document
        PDDocument doc = PDDocument.load(new File("./Media/TT.pdf"));

        // Get the first page of the document
        PDPage page = doc.getPage(0);

        // Get or create the acroform (interactive form) of the document
        PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
        if (acroForm == null) {
            acroForm = new PDAcroForm(doc);
            doc.getDocumentCatalog().setAcroForm(acroForm);
        }

        // Create a text field for the new text
        PDTextField textField = new PDTextField(acroForm);
        textField.setPartialName("newTextField");

        // Add the text field to the page
        acroForm.getFields().add(textField);

        // Create a page content stream to append the text
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);

        // Read the ticket information from the text file
        String ticketInfo = readTicketInfoFromFile(inputFile.getPath());

        // Split the ticket information into lines
        String[] lines = ticketInfo.split("\n");

        // Define the word positions
//        float[] xCoordinates = {165, 165, 165, 165, 765, 1365, 1365, 1365, 1980, 1980, 3240, 3240, 3240, 3240, 3240, 3590, 3590, 3900, 3900};
//        float[] yCoordinates = {1150, 850, 550, 250, 250, 850, 550, 250, 550, 250, 1165, 965, 765, 565, 340, 565, 340, 565, 340};
        float[] xCoordinates = {165,3240,165,3240,165,3240,165,3240,1365,3240,1365,1365,3900,1980,3590,1980,3900,765,3590};
        float[] yCoordinates = {1150,1165,850,965,550,765,250,340,550,565,850,250,565,550,565,250,340,250,340};
        
        
        int numLines = Math.min(lines.length, xCoordinates.length);
        for (int i = 0; i < numLines; i++) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 75);
            contentStream.newLineAtOffset(xCoordinates[i], yCoordinates[i]);
            contentStream.showText(lines[i]);
            contentStream.endText();
        }

        // Generate a unique ticket ID based on the ticket class
        int ticketId;
        if (ticketClass.equals("business")) {
            lastBusinessClassId++;
            ticketId = lastBusinessClassId;
        } else if (ticketClass.equals("economy")) {
            lastEconomyClassId++;
            ticketId = lastEconomyClassId;
        } else {
            throw new IllegalArgumentException("Invalid ticket class: " + ticketClass);
        }

        // Append the ticket ID to the PDF
        contentStream.beginText();
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 60);
        contentStream.newLineAtOffset(3800, 150);
        contentStream.showText("Ticket ID: " + ticketId);
        contentStream.endText();

        // Closing the content stream
        contentStream.close();

        // Saving the document
        String outputFileName = "./Media/Output/" + ticketId + ".pdf";;
        doc.save(new File(outputFileName));

        // Closing the document
        doc.close();
    }

    private static String readTicketInfoFromFile(String filePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }

        return stringBuilder.toString();
    }
    
    
    private static void deleteFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Check if the file is a text file
                    if (file.getName().endsWith(".txt")) {
                        try {
                            // Delete the file
                            file.delete();
                            System.out.println("Deleted file: " + file.getAbsolutePath());
                        } catch (SecurityException e) {
                            System.err.println("Failed to delete file: " + file.getAbsolutePath());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
}

