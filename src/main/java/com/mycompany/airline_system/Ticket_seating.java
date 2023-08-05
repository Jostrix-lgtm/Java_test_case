
package com.mycompany.airline_system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Ticket_seating {
    private static char[][] economySeats;
    private static char[][] businessSeats;
    private static final String ECONOMY_CLASS_FILE = "./Media/economy/seatingtemp.txt";
    private static final String BUSINESS_CLASS_FILE = "./Media/business/seatingtemp.txt";
    private static final String ECONOMY_CLASS_ADMIN_FILE = "./Media/admin/economy_admin_booked_seats.txt";
    private static final String BUSINESS_CLASS_ADMIN_FILE = "./Media/admin/business_admin_booked_seats.txt";

    public static void main(String[] args) {
        int rows = 5; // Number of rows in the airplane
        int economyColumns = 5; // Number of economy seats per row
        int businessColumns = 2; // Number of business seats per row

        initializeSeats(rows, economyColumns, businessColumns);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Class seating selection");
            System.out.println("-----------------------");
            System.out.println("1. Business Class");
            System.out.println("2. Economy Class");
            System.out.println("3. Exit");
            System.out.print("Enter your number: ");
            int seatClass = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character left by nextInt()

            switch (seatClass) {
                case 1:
                    displaySeatMap(businessSeats, "Business Class");
                    String identifierBusiness = "";
                    while (identifierBusiness.isEmpty()) {
                        System.out.print("Enter your unique identifier for Business Class: ");
                        identifierBusiness = scanner.nextLine();
                    }
                    confirmIdentifier(identifierBusiness, scanner);
                    loadUserBookedSeats(businessSeats, BUSINESS_CLASS_ADMIN_FILE, identifierBusiness);
                    bookSeats(businessSeats, "business", identifierBusiness);
                    // Save admin booked seats to admin text file
                    saveAdminBookedSeats(businessSeats, BUSINESS_CLASS_ADMIN_FILE);
                    break;
                case 2:
                    displaySeatMap(economySeats, "Economy Class");
                    String identifierEconomy = "";
                    while (identifierEconomy.isEmpty()) {
                        System.out.print("Enter your unique identifier for Economy Class: ");
                        identifierEconomy = scanner.nextLine();
                    }
                    confirmIdentifier(identifierEconomy, scanner);
                    loadUserBookedSeats(economySeats, ECONOMY_CLASS_ADMIN_FILE, identifierEconomy);
                    bookSeats(economySeats, "economy", identifierEconomy);
                    // Save admin booked seats to admin text file
                    saveAdminBookedSeats(economySeats, ECONOMY_CLASS_ADMIN_FILE);
                    break;
                case 3:
                    System.out.println("Thank you for using our Airline");
                    DataExtractor.main(new String[0]);
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        }
    }
    
    private static void confirmIdentifier(String identifier, Scanner scanner) {
        boolean confirmed = false;
        while (!confirmed) {
            System.out.print("Confirm your unique identifier (yes/no): ");
            String confirmation = scanner.nextLine().toLowerCase();
            if (confirmation.equals("yes")) {
                confirmed = true;
            } else if (confirmation.equals("no")) {
                System.out.println("Please enter your unique identifier again.");
                identifier = scanner.nextLine();
            } else {
                System.out.println("Invalid choice. Please enter 'yes' or 'no'.");
            }
        }
    }
    
    private static void initializeSeats(int rows, int economyColumns, int businessColumns) {
        economySeats = new char[rows][economyColumns];
        for (char[] row : economySeats) {
            Arrays.fill(row, 'O');
        }
        loadBookedSeats(ECONOMY_CLASS_FILE, economySeats);

        businessSeats = new char[rows][businessColumns];
        for (char[] row : businessSeats) {
            Arrays.fill(row, 'O');
        }
        loadBookedSeats(BUSINESS_CLASS_FILE, businessSeats);
    }
    
    private static void loadUserBookedSeats(char[][] seats, String fileName, String identifier) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(identifier)) {
                    String seatNumber = line.split(":")[1];
                    int row = Integer.parseInt(seatNumber.substring(0, seatNumber.length() - 1)) - 1;
                    char col = seatNumber.toUpperCase().charAt(seatNumber.length() - 1);
                    if (row >= 0 && row < seats.length && col >= 'A' && col < 'A' + seats[0].length) {
                        seats[row][col - 'A'] = 'X';
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading user booked seats: " + e.getMessage());
        }
    }

    
    private static void loadBookedSeats(String fileName, char[][] seats) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.matches("\\d+[A-Za-z]")) {
                    int row = Integer.parseInt(line.substring(0, line.length() - 1)) - 1;
                    char col = line.toUpperCase().charAt(line.length() - 1);
                    if (row >= 0 && row < seats.length && col >= 'A' && col < 'A' + seats[0].length) {
                        seats[row][col - 'A'] = 'X';
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading booked seats: " + e.getMessage());
        }
    }

   private static void displaySeatMap(char[][] seats, String className) {
    String fileName = className.equals("Business Class") ? BUSINESS_CLASS_ADMIN_FILE : ECONOMY_CLASS_ADMIN_FILE;

    System.out.println("\n" + className + " Seat Map:");
    System.out.print("  ");
    for (char i = 'A'; i < 'A' + seats[0].length; i++) {
        System.out.print(i + " ");
    }
    System.out.println();

    char[][] adminSeats = new char[seats.length][seats[0].length];
    for (int i = 0; i < seats.length; i++) {
        System.arraycopy(seats[i], 0, adminSeats[i], 0, seats[i].length);
    }
    loadAdminBookedSeats(adminSeats, fileName); // Load admin booked seats

    for (int i = 0; i < seats.length; i++) {
        System.out.print((i + 1) + " ");
        for (int j = 0; j < seats[i].length; j++) {
            char seatStatus = adminSeats[i][j] == 'O' ? seats[i][j] : adminSeats[i][j];
            System.out.print(seatStatus + " ");
        }
        System.out.println();
    }
}

    private static boolean isValidSeatNumber(String seatNumber, char[][] seats) {
        if (seatNumber.matches("\\d+[A-Za-z]")) {
            int row = Integer.parseInt(seatNumber.substring(0, seatNumber.length() - 1)) - 1;
            char col = seatNumber.toUpperCase().charAt(seatNumber.length() - 1);
            return row >= 0 && row < seats.length && col >= 'A' && col < 'A' + seats[0].length;
        }
        return false;
    }

    private static boolean isSeatAvailable(int row, int col, char[][] seats) {
        return seats[row][col] == 'O';
    }

    private static void updateSeatMap(char[][] seats, String className, String identifier) {
        System.out.println("\n" + className + " Seat Map:");
        System.out.print("  ");
        for (char i = 'A'; i < 'A' + seats[0].length; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        char[][] combinedSeats = new char[seats.length][seats[0].length];
        for (int i = 0; i < seats.length; i++) {
            System.arraycopy(seats[i], 0, combinedSeats[i], 0, seats[i].length);
        }

        loadAdminBookedSeats(seats, className); // Load admin booked seats
        loadUserBookedSeats(seats, className, identifier); // Load user booked seats

        for (int i = 0; i < seats.length; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < seats[i].length; j++) {
                char seatStatus = combinedSeats[i][j] == 'O' ? seats[i][j] : combinedSeats[i][j];
                System.out.print(seatStatus + " ");
            }
            System.out.println();
        }
    }
    
    private static void loadAdminBookedSeats(char[][] seats, String fileName) {
    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.matches("\\d+[A-Za-z]")) {
                int row = Integer.parseInt(line.substring(0, line.length() - 1)) - 1;
                char col = line.toUpperCase().charAt(line.length() - 1);
                if (row >= 0 && row < seats.length && col >= 'A' && col < 'A' + seats[0].length) {
                    seats[row][col - 'A'] = 'X';
                }
            }
        }
    } catch (IOException e) {
        System.out.println("Error loading admin booked seats: " + e.getMessage());
    }
}

    private static void saveUserBookedSeats(char[][] seats, String className, String identifier) {
        String fileName = "./Media/" + className + "/" + "seatingtemp.txt";
        
        try (FileWriter writer = new FileWriter(fileName, true)) { 
            for (int i = 0; i < seats.length; i++) {
                for (int j = 0; j < seats[i].length; j++) {
                    if (seats[i][j] == 'X') {
                        String seatNumber = (i + 1) + Character.toString((char) ('A' + j));
                        writer.write(seatNumber + "\n");
                    }
                }
            }

            System.out.println("Successfully saved user booked seats to " + fileName);

        } catch (IOException e) {
            System.out.println("Error saving user booked seats: " + e.getMessage());
        }
    }

    private static void saveAdminBookedSeats(char[][] seats, String className) {
        String fileName = className.equals("business") ? BUSINESS_CLASS_ADMIN_FILE : ECONOMY_CLASS_ADMIN_FILE;

        try (FileWriter writer = new FileWriter(fileName)) {
            for (int i = 0; i < seats.length; i++) {
                for (int j = 0; j < seats[i].length; j++) {
                    if (seats[i][j] == 'X') {
                        String seatNumber = (i + 1) + Character.toString((char) ('A' + j));
                        writer.write(seatNumber + "\n");
                    }
                }
            }

            System.out.println("Successfully saved admin booked seats to " + fileName);

        } catch (IOException e) {
            System.out.println("Error saving admin booked seats: " + e.getMessage());
        }
    }

    private static void bookSeats(char[][] seats, String className, String identifier) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter seat number (e.g., 2A), or 'DONE' to finish booking " + className + " seats: ");
            String seatNumber = scanner.nextLine().toUpperCase();

            if (seatNumber.equalsIgnoreCase("DONE")) {
                // Save booked seats to the user's text file before closing the scanner
                saveUserBookedSeats(seats, className, identifier);
                saveAdminBookedSeats(seats, className); // Save admin booked seats
                scanner.close();
                System.out.println("Thank you for booking. Enjoy your flight!");
                System.exit(0);
            }

            if (isValidSeatNumber(seatNumber, seats)) {
                int row = Integer.parseInt(seatNumber.substring(0, seatNumber.length() - 1)) - 1;
                int col = seatNumber.charAt(seatNumber.length() - 1) - 'A';

                if (isSeatAvailable(row, col, seats)) {
                    seats[row][col] = 'X';
                    System.out.println("Seat " + seatNumber + " has been booked.");
                } else {
                    System.out.println("Seat " + seatNumber + " is already booked or reserved.");
                }
            } else {
                System.out.println("Invalid seat number. Please try again.");
            }

            // Display the updated seat map after booking a seat
            updateSeatMap(seats, className, identifier);

            // Ask if the user wants to book more seats or finish booking
            while (true) {
                System.out.print("Do you want to book more seats? (yes/no): ");
                String continueBooking = scanner.nextLine().toLowerCase();
                if (continueBooking.equals("yes")) {
                    break; // Continue the outer loop to book more seats
                } else if (continueBooking.equals("no")) {
                    // Save booked seats to a text file before closing the scanner
                    saveUserBookedSeats(seats, className, identifier);
                    saveAdminBookedSeats(seats, className); // Save admin booked seats
                    scanner.close();
                    System.out.println("Thank you for booking. Enjoy your flight!");
                    DataExtractor.main(new String[0]);
                    System.exit(0);
                } else {
                    System.out.println("Invalid choice, please try again.");
                }
            }
        }
    }
}
    
