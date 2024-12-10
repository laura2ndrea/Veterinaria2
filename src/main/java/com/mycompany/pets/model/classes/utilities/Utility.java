
package com.mycompany.pets.model.classes.utilities;

import com.mycompany.pets.model.classes.enums.Status;
import java.util.Scanner;


public abstract class Utility {
 public static boolean askYesNo(Scanner scanner, String question) {
        int response;
        while (true) {
            System.out.println(question + "\n1. Yes, 2. No: ");
            try {
                response = Integer.parseInt(scanner.nextLine().trim());
                if (response == 1) {
                    return true; 
                } else if (response == 2) {
                    return false;
                } else {
                    System.out.println("Invalid input. Please enter '1' for Yes or '2' for No.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number ('1' or '2').");
            }
        }
    }
//  public static Status intoStatus(String status) {
//            try {
//                
//                
//                Status.status;
//                if (response == 1) {
//                    return true; 
//                } else if (response == 2) {
//                    return false;
//                } else {
//                    System.out.println("Invalid input. Please enter '1' for Yes or '2' for No.");
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid input. Please enter a valid number ('1' or '2').");
//            }
//        }
//    }
}
