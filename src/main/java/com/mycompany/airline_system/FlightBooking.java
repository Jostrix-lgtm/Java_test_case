
package com.mycompany.airline_system;


import java.awt.Container;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class FlightBooking {
    private static List<FlightData> flights = new ArrayList<>();
    static String selectedFlightId;
    static String passengerMail;

    public static void main(String[] args) {
        loadFlightsFromFiles();

        Scanner scanner = new Scanner(System.in);
        boolean isAdminMode = false;

        while (true) {
            displayFlights();
            System.out.println();

            if (!isAdminMode) {
                System.out.println("1. Book Flight ");
                System.out.println("2. Admin Mode ");
                System.out.print("Enter the number: ");
                int choice = scanner.nextInt();

                if (choice == 1) {
                    bookFlight(scanner);
                    System.out.println("Booking Done. Exiting...");
                    Ticket_seating.main(new String[0]);
                    break;
                } else if (choice == 2) {
                    isAdminMode = true;
                } else {
                    System.out.println("Invalid choice.");
                }
            } else {
                if (performAdminTasks(scanner)) {
                    System.out.println("Exiting Admin Mode...");
                    isAdminMode = false;
                }
            }

            System.out.println();
        }

        scanner.close();
    }

   private static void bookFlight(Scanner scanner) {
    FlightData selectedFlight = null;

    while (selectedFlight == null) {
        System.out.print("Enter the Flight ID to book: ");
        String flightId = scanner.next();

        selectedFlight = findFlightById(flightId);
        if (selectedFlight != null) {
            System.out.println("\nSelected Flight:\n");
            System.out.println(selectedFlight);
            System.out.println();

            System.out.print("Enter your mail: ");
            String mail = scanner.next();

            selectedFlightId = selectedFlight.getFlightId();
            passengerMail = mail;

            System.out.println("Booking confirmed for Flight ID: " + selectedFlight.getFlightId());
            System.out.println("Passenger Mail: " + passengerMail);
        } else {
            System.out.println("Flight not found. Please enter a valid Flight ID.");
        }
    }
}


    private static boolean performAdminTasks(Scanner scanner) {
        System.out.println("--- Admin Mode ---\n");
        System.out.println("1. Add New Flight");
       
        System.out.println("2. Exit Admin Mode");

        System.out.print("Enter a number: ");
        int choice = scanner.nextInt();

        if (choice == 1) {
            FlightData newFlight = createFlight(scanner);
            flights.add(newFlight);
            saveFlightToFile(newFlight);
            System.out.println("\nNew flight added successfully:\n");
            System.out.println(newFlight);
            return performAdminTasks(scanner); // Continue adding flights
    
        } else if (choice == 2) {
            return true; // Exit admin mode
        } else {
            System.out.println("Invalid choice.");
        }
        return false;
    }

    private static FlightData createFlight(Scanner scanner) {
        System.out.print("Enter flight ID: ");
        String flightId = scanner.next();

        System.out.print("Enter flight name: ");
        String flightName = scanner.next();

        System.out.print("Enter departure location: ");
        String departureLocation = scanner.next();

        System.out.print("Enter arrival location: ");
        String arrivalLocation = scanner.next();

        System.out.print("Enter departure date: ");
        String departureDate = scanner.next();

        System.out.print("Enter departure time: ");
        String departureTime = scanner.next();

        System.out.print("Enter arrival date: ");
        String arrivalDate = scanner.next();

        System.out.print("Enter arrival time: ");
        String arrivalTime = scanner.next();

        System.out.print("Enter gate number: ");
        String gateNumber = scanner.next();

        return new FlightData(flightId, flightName, departureLocation, arrivalLocation,
                departureDate, departureTime, arrivalDate, arrivalTime, gateNumber);
    }

    private static void loadFlightsFromFiles() {
        File folder = new File("FlightData"); // Assuming flight data files are in a folder named "FlightData"
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        String flightId = reader.readLine();
                        String flightName = reader.readLine();
                        String departureLocation = reader.readLine();
                        String arrivalLocation = reader.readLine();
                        String departureDate = reader.readLine();
                        String departureTime = reader.readLine();
                        String arrivalDate = reader.readLine();
                        String arrivalTime = reader.readLine();
                        String gateNumber = reader.readLine();

                        FlightData flight = new FlightData(flightId, flightName, departureLocation, arrivalLocation,
                                departureDate, departureTime, arrivalDate, arrivalTime, gateNumber);
                        flights.add(flight);
                    } catch (IOException e) {
                        System.out.println("Error reading file: " + file.getName());
                    }
                }
            }
        } else {
            System.out.println("No flight data files found.");
        }
    }

    private static void saveFlightToFile(FlightData flight) {
        String folderPath = "./FlightData/"; // Specify the desired folder path here
        String filename = folderPath + "flight_" + flight.getFlightId() + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(flight.getFlightId());
            writer.println(flight.getFlightName());
            writer.println(flight.getDepartureLocation());
            writer.println(flight.getArrivalLocation());
            writer.println(flight.getDepartureDate());
            writer.println(flight.getDepartureTime());
            writer.println(flight.getArrivalDate());
            writer.println(flight.getArrivalTime());
            writer.println(flight.getGateNumber());
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void displayFlights() {
        System.out.println("Available Flights:\n");

        for (FlightData flight : flights) {
            System.out.println(flight);
            System.out.println();
        }
    }

    private static FlightData findFlightById(String flightId) {
        for (FlightData flight : flights) {
            if (flight.getFlightId().equals(flightId)) {
                return flight;
            }
        }
        return null;
    }
}

class FlightData {
    private String flightId;
    private String flightName;
    private String departureLocation;
    private String arrivalLocation;
    private String departureDate;
    private String departureTime;
    private String arrivalDate;
    private String arrivalTime;
    private String gateNumber;

    FlightData(String flightId, String flightName, String departureLocation, String arrivalLocation,
               String departureDate, String departureTime, String arrivalDate, String arrivalTime,
               String gateNumber) {
        this.flightId = flightId;
        this.flightName = flightName;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.arrivalDate = arrivalDate;
        this.arrivalTime = arrivalTime;
        this.gateNumber = gateNumber;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getFlightName() {
        return flightName;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public String getArrivalLocation() {
        return arrivalLocation;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getGateNumber() {
        return gateNumber;
    }

    @Override
    public String toString() {
        return "Flight ID: " + flightId +
                "\nFlight Name: " + flightName +
                "\nDeparture: " + departureLocation +
                "\nArrival: " + arrivalLocation +
                "\nDeparture Date: " + departureDate +
                "\nDeparture Time: " + departureTime +
                "\nArrival Date: " + arrivalDate +
                "\nArrival Time: " + arrivalTime +
                "\nGate Number: " + gateNumber;
    }

}
