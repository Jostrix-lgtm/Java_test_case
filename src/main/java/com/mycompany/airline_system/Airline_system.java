
package com.mycompany.airline_system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Airline_system {
    private static boolean isLoggedIn = false;
    private static String loggedInUser = "";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Airline Booking System!");
        System.out.println("--------------------------------------");

        while (true) {
            if (isLoggedIn) {
                System.out.println("How can we help you?");
                System.out.println("1. Set up account detail");
                System.out.println("2. Buy Airline Ticket");
                System.out.println("3. Logout");
                System.out.println("4. Exit");
                System.out.print("Enter your number: ");
                int choiceNum = scanner.nextInt();

                switch (choiceNum) {
                    case 1:
                        setUpAcc();
                        break;
                    case 2:
                        buyAirlineTicket();
                        break;
                    case 3:
                        isLoggedIn = false;
                        loggedInUser = "";
                        break;
                    case 4:
                        System.out.println("Thank you for using our Airline.");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                        break;
                }
            } else {
                System.out.println("");
                System.out.println("How can we help you?");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your number: ");
                int choiceNum = scanner.nextInt();

                switch (choiceNum) {
                    case 1:
                        registerUser();
                        break;
                    case 2:
                        while (true) {
                            isLoggedIn = loginUser();
                            if (isLoggedIn || !isLoggedIn) {
                                break;
                            }
                        }
                        break;
                    case 3:
                        System.out.println("Thank you for using our Airline.");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice, please try again.");
                        break;
                }
            }
        }
    }

    public static void registerUser() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("");
        System.out.println("User registration");
        System.out.println("------------------------");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.println("");

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.println("");

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        System.out.println("");

        String fileName = "./UserData/" + email.toLowerCase() + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Name: " + name);
            writer.println("Email: " + email);
            writer.println("Password: " + password);
            System.out.println("Registration complete for " + name + ". Please login again.");
        } catch (IOException e) {
            System.out.println("Error while creating user file: " + e.getMessage());
        }
    }

    public static boolean loginUser() {
        Scanner scanner = new Scanner(System.in);
        

        while (true) {
            System.out.println("");
            System.out.println("User login");
            System.out.println("-----------------");

            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            System.out.println("");
           

            System.out.print("Enter your password: ");
            String password = scanner.nextLine();
            System.out.println("");

            if (isValidLogin(email, password)) {
                System.out.println("Login successful. Welcome back!");
                loggedInUser = email;
                return true;
            } else {
                System.out.println("Invalid email or password.");
                System.out.println("1. Try again");
                System.out.println("2. Back to main menu");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        break;
                    case 2:
                        return false;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }
        }
    }

    private static boolean isValidLogin(String email, String password) {
        String fileName = "./UserData/" + email.toLowerCase() + ".txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Password:")) {
                    String storedPassword = line.substring("Password:".length()).trim();
                    if (storedPassword.equals(password)) {
                        return true;
                    }
                    break; // No need to continue reading the file
                }
            }
        } catch (IOException e) {
            System.out.println("Error while reading user data from file: " + e.getMessage());
        }
        return false;
    }

    public static void setUpAcc() {
        System.out.println("Set Up Acc");
         CustomerDetailsToFile.main(new String[0]);
    }

    public static void buyAirlineTicket() {
        System.out.println("Thank you for using the Airline Booking System.");
        FlightBooking.main(new String[0]);
    }
}
