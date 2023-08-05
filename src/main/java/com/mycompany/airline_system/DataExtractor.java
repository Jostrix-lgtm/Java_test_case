
package com.mycompany.airline_system;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

public class DataExtractor {
    public static void main(String[] args) {
        String userDataFolderPath = "./UserData/";
        String flightDataFolderPath = "./FlightData/";
        String seatingTempDirectory1 = "./Media/business/";
        String seatingTempDirectory2 = "./Media/economy/";

        String specificUserFile = FlightBooking.passengerMail.toLowerCase() + ".txt";
        String specificFlightFile = "flight_" + FlightBooking.selectedFlightId + ".txt";

        String seatingTempFilePath = findSeatingTempFile(seatingTempDirectory1, seatingTempDirectory2);

        if (seatingTempFilePath != null) {
            int numLines = getNumberOfLines(seatingTempFilePath);

            try (BufferedReader seatingReader = new BufferedReader(new FileReader(seatingTempFilePath))) {
                for (int i = 0; i < numLines; i++) {
                    String currentSeatingLine = seatingReader.readLine();
                    String outputFileName = "extracted" + i + ".txt";
                    extractData(userDataFolderPath + specificUserFile, flightDataFolderPath + specificFlightFile, outputFileName, currentSeatingLine, seatingTempFilePath);
                    
                }
            } catch (IOException e) {
                System.out.println("Error reading seatingtemp.txt: " + e.getMessage());
            }
        } else {
            System.out.println("Error: seatingtemp.txt not found in either directory.");
        }
        try {
            TicketBooking.main(args);
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static String findSeatingTempFile(String directory1, String directory2) {
        String seatingTempFilePath = null;

        String seatingTempFile1 = directory1 + "seatingtemp.txt";
        String seatingTempFile2 = directory2 + "seatingtemp.txt";

        File file1 = new File(seatingTempFile1);
        File file2 = new File(seatingTempFile2);

        if (file1.exists()) {
            seatingTempFilePath = seatingTempFile1;
        } else if (file2.exists()) {
            seatingTempFilePath = seatingTempFile2;
        }

        return seatingTempFilePath;
    }

    public static int getNumberOfLines(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        } catch (IOException e) {
            System.out.println("Error counting lines: " + e.getMessage());
        }
        return 0;
    }

    public static void extractData(String userFilePath, String flightFilePath, String outputFilename, String currentSeatingLine, String seatingTempFilePath) {
        try (BufferedReader userReader = new BufferedReader(new FileReader(userFilePath));
             BufferedReader flightReader = new BufferedReader(new FileReader(flightFilePath))) {

            String destinationFolder = determineDestinationFolder(seatingTempFilePath);

            if (destinationFolder != null) {
                String outputFilePath = "./extractedData/" + destinationFolder + "/" + outputFilename;
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

                // Extract user data
                String name = userReader.readLine();
                String email = userReader.readLine();
                String password = userReader.readLine();

                while (name != null && email != null && password != null) {
                    writeToFile(name, writer);
                    writeToFile(name, writer);
                    

                    name = userReader.readLine();
                    email = userReader.readLine();
                    password = userReader.readLine();
                }

                // Extract flight data
                String flightid = flightReader.readLine();
                String flightname = flightReader.readLine();
                String departurelocation = flightReader.readLine();
                String arrivallocation = flightReader.readLine();
                String date = flightReader.readLine();
                String departureTime = flightReader.readLine();
                String seat = flightReader.readLine();
                String arrivaltime = flightReader.readLine();
                String gate = flightReader.readLine();

                while (flightid != null && flightname != null && departurelocation != null &&
                        arrivallocation != null && date != null && departureTime != null &&
                        seat != null && arrivaltime != null && gate != null) {
                        writeToFile(departurelocation, writer);
                        writeToFile(departurelocation, writer);
                        writeToFile(arrivallocation, writer);
                        writeToFile(arrivallocation, writer);
                        writeToFile(flightid, writer);
                        writeToFile(flightid, writer);
                        writeToFile(date, writer);
                        writeToFile(date, writer);
                        writeToFile(flightname, writer);
                        writeToFile(gate, writer);
                        writeToFile(gate, writer);
                        writeToFile(departureTime, writer);
                        writeToFile(departureTime, writer);
                        writeToFile(arrivaltime, writer);
                        writeToFile(arrivaltime, writer);

                    flightid = flightReader.readLine();
                    flightname = flightReader.readLine();
                    departurelocation = flightReader.readLine();
                    arrivallocation = flightReader.readLine();
                    date = flightReader.readLine();
                    departureTime = flightReader.readLine();
                    seat = flightReader.readLine();
                    arrivaltime = flightReader.readLine();
                    gate = flightReader.readLine();
                }

                // Append currentSeatingLine to the bottom of the file
                writeToFile(currentSeatingLine, writer);
                writeToFile(currentSeatingLine, writer);

                writer.close();
            } else {
                System.out.println("Error: Invalid or unknown seatingTempFilePath.");
            }
        } catch (IOException e) {
            System.out.println("Error extracting data: " + e.getMessage());
        }
    }

    public static String determineDestinationFolder(String seatingTempFilePath) {
        if (seatingTempFilePath.contains("/business/")) {
            return "business";
        } else if (seatingTempFilePath.contains("/economy/")) {
            return "economy";
        }

        return null; // Invalid or unknown seatingTempFilePath
    }

    public static void writeToFile(String data, BufferedWriter writer) {
        try {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    
}

