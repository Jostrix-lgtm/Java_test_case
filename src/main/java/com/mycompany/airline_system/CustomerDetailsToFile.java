package com.mycompany.airline_system;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class CustomerDetailsToFile {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter full name: ");
        String fullName = scanner.nextLine();

        System.out.print("Enter date of birth: ");
        String dateOfBirth = scanner.nextLine();

        System.out.print("Enter nationality: ");
        String nationality = scanner.nextLine();

        System.out.print("Enter mobile phone: ");
        String mobilePhone = scanner.nextLine();

        System.out.print("Enter gender: ");
        String gender = scanner.nextLine();

        String folderPath = "./CustomerData/"; // Replace with the actual folder path

        // Create a unique filename based on the customer's name
        String uniqueFilename = fullName.replaceAll("\\s+", "_") + ".txt";
        String filePath = folderPath + "/" + uniqueFilename;

        // Create the file
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("Full Name: " + fullName);
            printWriter.println("Date of Birth: " + dateOfBirth);
            printWriter.println("Nationality: " + nationality);
            printWriter.println("Mobile Phone: " + mobilePhone);
            printWriter.println("Gender: " + gender);

            printWriter.close();
            System.out.println("Customer details have been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
