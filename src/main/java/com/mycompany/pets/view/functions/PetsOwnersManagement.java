package com.mycompany.pets.view.functions;

import com.mycompany.pets.controller.ControllerAnimal;
import static com.mycompany.pets.controller.ControllerAnimal.insertAnimal;
import com.mycompany.pets.model.classes.Cat;
import com.mycompany.pets.model.classes.Characteristic;
import com.mycompany.pets.model.classes.Dog;
import com.mycompany.pets.model.classes.Identifier;
import com.mycompany.pets.model.classes.superclasses.Animal;
import com.mycompany.pets.model.classes.superclasses.Type;
import static com.mycompany.pets.model.classes.superclasses.Type.assignType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PetsOwnersManagement {

    public static void createAnimal(Scanner scanner) {
        // Mostrar la tabla de Species y asignar el tipo
        Type speciesType = assignType(scanner, "Species");
        scanner.nextLine(); // Limpiar el buffer
        
        // Solicitar datos básicos del animal
        System.out.print("Enter the animal's name: ");
        String name = scanner.nextLine();

        System.out.print("Enter the animal's birth date (YYYY-MM-DD): ");
        String dateBirth = scanner.nextLine();

        System.out.print("Enter the animal's sex (MALE/FEMALE): ");
        String sex = scanner.nextLine();

        System.out.print("Enter the animal's weight (kg): ");
        double weight = scanner.nextDouble();
        scanner.nextLine(); // Limpiar el buffer

        System.out.print("Enter any special conditions (or leave blank): ");
        String conditions = scanner.nextLine();

        System.out.print("Enter allergies (or leave blank): ");
        String allergies = scanner.nextLine();

        System.out.print("Is the animal available for adoption or sale? (ADOPTION, FOR_SALE, NONE): ");
        String isAvailable = scanner.nextLine().trim().toUpperCase();

        System.out.print("Enter the URL of the animal's photo: ");
        String URLPhoto = scanner.nextLine();
        
        System.out.print("Enter the ID of the owner: ");
        int owner = scanner.nextInt();
        scanner.nextLine(); // Limpiar el buffer
        
        // Crear el animal específico según la especie seleccionada
        Animal newAnimal;
        if (speciesType.getType().equalsIgnoreCase("dog")) {
            newAnimal = new Dog(id, name, dateBirth, sex, weight, conditions, allergies, isAvailable, URLPhoto, owner, null);
        } else if (speciesType.getType().equalsIgnoreCase("cat")) {
            newAnimal = new Cat(id, name, dateBirth, sex, weight, conditions, allergies, isAvailable, URLPhoto, owner, null);
        } else {
            System.out.println("Unknown species type.");
            return; // Termina el método si no se reconoce la especie
        }

        // Inicializar características si no están definidas
        if (newAnimal.getCharacteristics() == null) {
            newAnimal.setCharacteristics(new ArrayList<>());
        }

        // Solicitar características específicas según el tipo de animal
        List<Characteristic> characteristics = new ArrayList<>();
        System.out.println("Enter specific characteristics for the animal:");
        for (Characteristic characteristic : newAnimal.getCharacteristics()) {
            System.out.print("Enter value for " + characteristic.getName() + ": ");
            String value = scanner.nextLine();
            characteristic.setValue(value);  // Actualizar el valor de la característica

            // Agregar la característica a la lista
            characteristics.add(characteristic);
        }
        newAnimal.setCharacteristics(characteristics);  // Establecer la lista actualizada de características

        // Preguntar si el animal tiene identificación
        System.out.print("Does the animal have an identifier? (yes/no): ");
        String hasIdentifier = scanner.nextLine().trim().toLowerCase();

        if (hasIdentifier.equals("yes")) {
            // Asignar tipo de identificador
            Type identifierType = assignType(scanner, "Identifier");
            scanner.nextLine(); // Limpiar el buffer

            System.out.print("Enter the identifier number: ");
            String identifierNumber = scanner.nextLine();
            scanner.nextLine(); // Limpiar el buffer
            System.out.println(identifierNumber);
        
        // Asignar el identificador al perro
            // Crear y asignar identificador
            Identifier identifier = new Identifier();
            identifier.setNumber(identifierNumber);  // Asignar el número del identificador correctamente
            identifier.setIdTypeIdentifier(identifierType.getId());
            newAnimal.setIdentifier(identifier);  // Asignar el identificador al animal

            // Verificar si el identificador está asignado correctamente
            if (newAnimal.getIdentifier() != null) {
                System.out.println("Identifier assigned: " + newAnimal.getIdentifier().getNumber());
            } else {
                System.out.println("No identifier assigned.");
            }
        } else {
            newAnimal.setIdentifier(null); // Sin identificación
        }

        // Verificar si el identificador se ha guardado correctamente
        System.out.println("Animal identifier: " + (newAnimal.getIdentifier() != null ? newAnimal.getIdentifier().getNumber() : "No identifier"));

        // Llamar al método insertAnimal para guardar en la base de datos
        if (insertAnimal(newAnimal)) {
            System.out.println("The animal has been successfully added to the database!");
        } else {
            System.out.println("There was an error adding the animal to the database.");
        }
    }
    
    public static void showAllAnimals() throws SQLException {
    ControllerAnimal controllAnimal = new ControllerAnimal();
    List<Animal> animals = controllAnimal.listAnimals() ;  // Get all animals

    if (animals.isEmpty()) {
        System.out.println("No animals registered.");
    } else {
        System.out.println("\n--- Animal List ---");
        for (Animal animal : animals) {
            System.out.println("\nAnimal found:");
            System.out.println("Name: " + animal.getName());
            System.out.println("Birth Date: " + animal.getBirthDate());
            System.out.println("Sex: " + animal.getSex());
            System.out.println("Weight: " + animal.getWeight());
            System.out.println("Conditions: " + animal.getConditions());
            System.out.println("Allergies: " + animal.getAllergies());
            System.out.println("Available: " + animal.getIsAvailable());
            System.out.println("Photo URL: " + animal.getUrlPhoto());
            System.out.println("Owner: " + animal.getOwner());
             System.out.println("Identificador: " + (animal.getIdentifier() != null ? animal.getIdentifier().getNumber() : "Without identifier"));

            // Displaying characteristics of the animal
            System.out.println("Characteristics:");
            for (Characteristic characteristic : animal.getCharacteristics()) {
                System.out.println(" " + characteristic.getName() + ": " + characteristic.getValue());
            }

            System.out.println("---------------");
        }
    }
}
}