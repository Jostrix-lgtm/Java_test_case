package com.mycompany.airline_system;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Receipt {
    private static final String OUTPUT_FOLDER = "./Media/Output";

    public static void main(String[] args) {
        File outputFolder = new File(OUTPUT_FOLDER);
        String[] items = readFilenames(outputFolder);

        // Generate the receipt number
        String receiptNumber = generateReceiptNumber();

        // Get the current date
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String receiptDate = dateFormat.format(currentDate);

        // Generate the receipt PDF
        String receiptPdf = "./Media/Receipt.pdf";
        generateReceiptPDF(receiptPdf, receiptNumber, receiptDate, items);

        // Print the receipt
        printReceipt(receiptNumber, receiptDate, items);
    }

    private static String[] readFilenames(File outputFolder) {
        List<String> itemsList = new ArrayList<>();
        File[] files = outputFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String filename = file.getName().trim();
                    String ticketClass = getTicketClass(filename);
                    if (ticketClass != null) {
                        String idNumber = getIdNumber(filename);
                        String item = ticketClass + "_" + idNumber;
                        itemsList.add(item);
                    }
                }
            }
        } else {
            System.err.println("Failed to read files from the output folder: " + OUTPUT_FOLDER);
        }
        return itemsList.toArray(new String[0]);
    }

    private static String getTicketClass(String filename) {
        String ticketClass = null;
        if (filename.startsWith("2")) {
            ticketClass = "Economic Class";
        } else if (filename.startsWith("9")) {
            ticketClass = "Business Class";
        } else {
            System.err.println("Invalid ticket ID format: " + filename);
        }
        return ticketClass;
    }

    private static String getIdNumber(String filename) {
        String idNumber = null;
        try {
            idNumber = filename.replaceAll("[^\\d]", ""); // Extract digits from the filename
        } catch (NumberFormatException e) {
            System.err.println("Failed to extract ID number from filename: " + filename);
        }
        return idNumber;
    }

    private static String generateReceiptNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        String timestamp = dateFormat.format(new Date());

        Random random = new Random();
        int randomNum = random.nextInt(99999);

        return timestamp + "-" + randomNum;
    }

    private static void printReceipt(String receiptNumber, String receiptDate, String[] items) {
        System.out.println("Receipt");
        System.out.println("-------");
        System.out.println("Receipt Number: " + receiptNumber);
        System.out.println("Receipt Date: " + receiptDate);
        System.out.println("----------------------------------------");
        System.out.println("Item                       Price");
        System.out.println("----------------------------------------");
        double subtotal = 0.0;
        DecimalFormat df = new DecimalFormat("#0.00");
        for (String item : items) {
            String ticketClass = item.split("_")[0];
            String idNumber = item.split("_")[1];
            double ticketPrice = (ticketClass.equals("Economic Class")) ? 1000.0 : 3000.0;

            // Replace tab character with spaces
            item = item.replaceAll("    ", " ");

            System.out.println(item + "     $" + df.format(ticketPrice));
            subtotal += ticketPrice;
        }
        System.out.println("----------------------------------------");
        double taxRate = 0.06;
        double taxAmount = subtotal * taxRate;
        System.out.println("Subtotal:                  $" + df.format(subtotal));
        System.out.println("Tax (6%):                  $" + df.format(taxAmount));
        System.out.println("Total:                     $" + df.format(subtotal + taxAmount));
    }

    private static void generateReceiptPDF(String receiptPdf, String receiptNumber, String receiptDate, String[] items) {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

            float yStart = page.getMediaBox().getHeight() - 50; // Initial vertical position
            float yPosition = yStart;
            float margin = 50; // Left margin

            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Receipt");
            contentStream.newLineAtOffset(0, -14);
            contentStream.newLine();
            contentStream.showText("------------");
            contentStream.newLineAtOffset(0, -14);
            contentStream.newLine();
            contentStream.showText("Receipt Number: " + receiptNumber);
            contentStream.newLineAtOffset(0, -14);
            contentStream.newLine();
            contentStream.showText("Receipt Date: " + receiptDate);
            contentStream.newLineAtOffset(0, -14);
            contentStream.newLine();
            contentStream.showText("-------------------------------------------------------");
            contentStream.newLineAtOffset(0, -14);
            contentStream.newLine();
            contentStream.showText("Item                                          Price");
            contentStream.newLineAtOffset(0, -14);
            contentStream.newLine();
            contentStream.showText("-------------------------------------------------------");
            yPosition -= 14;
            contentStream.newLine();
            yPosition -= 14; // Vertical spacing

            double subtotal = 0.0;
            DecimalFormat df = new DecimalFormat("#0.00");
            for (String item : items) {
                String ticketClass = item.split("_")[0];
                String idNumber = item.split("_")[1];
                double ticketPrice = (ticketClass.equals("Economic Class")) ? 1000.0 : 3000.0;

                // Replace tab character with spaces
                item = item.replaceAll("    ", " ");

                contentStream.newLineAtOffset(0, -14); // Move to a new line
                contentStream.showText(item + "    $" + df.format(ticketPrice));
                subtotal += ticketPrice;
            }
            contentStream.newLineAtOffset(0, -14); // Move to a new line
            contentStream.showText("-------------------------------------------------------");
            contentStream.newLineAtOffset(0, -14); // Move to a new line

            double taxRate = 0.06;
            double taxAmount = subtotal * taxRate;
            contentStream.showText("Subtotal:                                 $" + df.format(subtotal));
            contentStream.newLineAtOffset(0, -14); // Move to a new line
            contentStream.showText("Tax (6%):                                 $" + df.format(taxAmount));
            contentStream.newLineAtOffset(0, -14); // Move to a new line
            contentStream.showText("Total:                                       $" + df.format(subtotal + taxAmount));
            contentStream.newLine();
            contentStream.endText();
            contentStream.close();

            document.save(receiptPdf);
            document.close();

            System.out.println("Receipt PDF saved to " + receiptPdf);
        } catch (IOException e) {
            System.out.println("Error generating receipt PDF: " + e.getMessage());
        }
    }
}

//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.apache.pdfbox.pdmodel.PDPage;
//import org.apache.pdfbox.pdmodel.common.PDRectangle;
//import org.apache.pdfbox.pdmodel.PDPageContentStream;
//import org.apache.pdfbox.pdmodel.font.PDType1Font;
//
//import java.io.IOException;
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Random;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class Receipt {
//    private static final String OUTPUT_FOLDER = "./Media/Output";
//
//    public static void main(String[] args) {
//        File outputFolder = new File(OUTPUT_FOLDER);
//        String[] items = readFilenames(outputFolder);
//
//        // Generate the invoice number
//        String invoiceNumber = generateInvoiceNumber();
//
//        // Get the current date
//        Date currentDate = new Date();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
//        String invoiceDate = dateFormat.format(currentDate);
//
//        // Generate the invoice PDF
//        String invoicePdf = "./Media/Invoice.pdf";
//        generateInvoicePDF(invoicePdf, invoiceNumber, invoiceDate, items);
//
//        // Print the invoice
//        printInvoice(invoiceNumber, invoiceDate, items);
//    }
//
//    private static String[] readFilenames(File outputFolder) {
//        List<String> itemsList = new ArrayList<>();
//        File[] files = outputFolder.listFiles();
//        if (files != null) {
//            for (File file : files) {
//                if (file.isFile()) {
//                    String filename = file.getName().trim();
//                    String ticketClass = getTicketClass(filename);
//                    if (ticketClass != null) {
//                        String idNumber = getIdNumber(filename);
//                        String item = ticketClass + "_" + idNumber;
//                        itemsList.add(item);
//                    }
//                }
//            }
//        } else {
//            System.err.println("Failed to read files from the output folder: " + OUTPUT_FOLDER);
//        }
//        return itemsList.toArray(new String[0]);
//    }
//
//    private static String getTicketClass(String filename) {
//        String ticketClass = null;
//        if (filename.startsWith("2")) {
//            ticketClass = "Economic Class";
//        } else if (filename.startsWith("9")) {
//            ticketClass = "Business Class";
//        } else {
//            System.err.println("Invalid ticket ID format: " + filename);
//        }
//        return ticketClass;
//    }
//
//    private static String getIdNumber(String filename) {
//        String idNumber = null;
//        try {
//            idNumber = filename.replaceAll("[^\\d]", ""); // Extract digits from the filename
//        } catch (NumberFormatException e) {
//            System.err.println("Failed to extract ID number from filename: " + filename);
//        }
//        return idNumber;
//    }
//
//    private static String generateInvoiceNumber() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
//        String timestamp = dateFormat.format(new Date());
//
//        Random random = new Random();
//        int randomNum = random.nextInt(99999);
//
//        return timestamp + "-" + randomNum;
//    }
//
//    private static void printInvoice(String invoiceNumber, String invoiceDate, String[] items) {
//        System.out.println("Invoice");
//        System.out.println("-------");
//        System.out.println("Invoice Number: " + invoiceNumber);
//        System.out.println("Invoice Date: " + invoiceDate);
//        System.out.println("----------------------------------------");
//        System.out.println("Item                       Price");
//        System.out.println("----------------------------------------");
//        double subtotal = 0.0;
//        DecimalFormat df = new DecimalFormat("#0.00");
//        for (String item : items) {
//            String ticketClass = item.split("_")[0];
//            String idNumber = item.split("_")[1];
//            double ticketPrice = (ticketClass.equals("Economic Class")) ? 1000.0 : 3000.0;
//
//            // Replace tab character with spaces
//            item = item.replaceAll("    ", " ");
//
//            System.out.println(item + "     $" + df.format(ticketPrice));
//            subtotal += ticketPrice;
//        }
//        System.out.println("----------------------------------------");
//        double taxRate = 0.06;
//        double taxAmount = subtotal * taxRate;
//        System.out.println("Subtotal: $" + df.format(subtotal));
//        System.out.println("Tax (6%): $" + df.format(taxAmount));
//        System.out.println("Total: $" + df.format(subtotal + taxAmount));
//    }
//
//    private static void generateInvoicePDF(String invoicePdf, String invoiceNumber, String invoiceDate, String[] items) {
//        try {
//            PDDocument document = new PDDocument();
//            PDPage page = new PDPage(PDRectangle.A4);
//            document.addPage(page);
//
//            PDPageContentStream contentStream = new PDPageContentStream(document, page);
//            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
//
//            float yStart = page.getMediaBox().getHeight() - 50; // Initial vertical position
//            float yPosition = yStart;
//            float margin = 50; // Left margin
//
//            contentStream.beginText();
//            contentStream.newLineAtOffset(margin, yPosition);
//            contentStream.showText("Invoice");
//            contentStream.newLineAtOffset(0, -14); 
//            contentStream.newLine();
//            contentStream.showText("-------");
//            contentStream.newLineAtOffset(0, -14); 
//            contentStream.newLine();
//            contentStream.showText("Invoice Number: " + invoiceNumber);
//            contentStream.newLineAtOffset(0, -14); 
//            contentStream.newLine();
//            contentStream.showText("Invoice Date: " + invoiceDate);
//            contentStream.newLineAtOffset(0, -14); 
//            contentStream.newLine();
//            contentStream.showText("----------------------------------------");
//            contentStream.newLineAtOffset(0, -14); 
//            contentStream.newLine();
//            contentStream.showText("Item                       Price");
//            contentStream.newLineAtOffset(0, -14); 
//            contentStream.newLine();
//            contentStream.showText("----------------------------------------");
//            yPosition -= 14;
//            contentStream.newLine();
//            yPosition -= 14; // Vertical spacing
//
//            double subtotal = 0.0;
//            DecimalFormat df = new DecimalFormat("#0.00");
//            for (String item : items) {
//                String ticketClass = item.split("_")[0];
//                String idNumber = item.split("_")[1];
//                double ticketPrice = (ticketClass.equals("Economic Class")) ? 1000.0 : 3000.0;
//
//                // Replace tab character with spaces
//                item = item.replaceAll("    ", " ");
//
//                contentStream.newLineAtOffset(0, -14); // Move to a new line
//                contentStream.showText(item + "     $" + df.format(ticketPrice));
//                subtotal += ticketPrice;
//            }
//            contentStream.newLineAtOffset(0, -14); // Move to a new line
//            contentStream.showText("----------------------------------------");
//            contentStream.newLineAtOffset(0, -14); // Move to a new line
//
//            double taxRate = 0.06;
//            double taxAmount = subtotal * taxRate;
//            contentStream.showText("Subtotal: $" + df.format(subtotal));
//            contentStream.newLineAtOffset(0, -14); // Move to a new line
//            contentStream.showText("Tax (6%): $" + df.format(taxAmount));
//            contentStream.newLineAtOffset(0, -14); // Move to a new line
//            contentStream.showText("Total: $" + df.format(subtotal + taxAmount));
//            contentStream.newLine();
//            contentStream.endText();
//            contentStream.close();
//
//            document.save(invoicePdf);
//            document.close();
//
//            System.out.println("Invoice PDF saved to " + invoicePdf);
//        } catch (IOException e) {
//            System.out.println("Error generating invoice PDF: " + e.getMessage());
//        }
//    }
//}
