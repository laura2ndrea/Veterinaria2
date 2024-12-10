package com.mycompany.pets.model.classes.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public abstract class UtilityTime {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime changeSqlDate(String sqlDate) {
        try {
            return LocalDateTime.parse(sqlDate, TIME_FORMATTER);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error: invalid SQL date format. Expected 'yyyy-MM-dd HH:mm:ss'.", e);
        }
    }

    public static String convertDateString(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        return date.format(formatter);
    }

    public static String convertDateString(String dateString) {
        LocalDateTime date = LocalDateTime.parse(dateString);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm");
        return date.format(formatter);
    }
    
    public static LocalDateTime getValidatedDate(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentDate = null;
        while (appointmentDate == null) {
            System.out.print("Enter the date and time (format: yyyy-MM-dd HH:mm): ");
            String input = scanner.nextLine();
            try {
                appointmentDate = LocalDateTime.parse(input, formatter);
                if (appointmentDate.isBefore(now)) {
                    System.out.println("The date cannot be in the past. Please enter a future date.");
                    appointmentDate = null;
                } else if (appointmentDate.getDayOfWeek().getValue() == 6 || appointmentDate.getDayOfWeek().getValue() == 7) {
                    System.out.println("Appointments cannot be scheduled on weekends. Please choose a weekday.");
                    appointmentDate = null;
                } else {
                    int hour = appointmentDate.getHour();
                    int minute = appointmentDate.getMinute();
                    if (hour < 8 || (hour >= 17 && minute > 30)) {
                        System.out.println("Appointments must be scheduled between 08:00 AM and 05:30 PM. Please try again.");
                        appointmentDate = null;
                    }
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in the format yyyy-MM-dd HH:mm.");
            }
        }
        System.out.println("Validated appointment date: " + appointmentDate.format(formatter));
        return appointmentDate;
    }
}
