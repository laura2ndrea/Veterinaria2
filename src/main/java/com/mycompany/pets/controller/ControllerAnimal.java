package com.mycompany.pets.controller;

import com.mycompany.pets.model.classes.Cat;
import com.mycompany.pets.model.classes.Characteristic;
import com.mycompany.pets.model.classes.Dog;
import com.mycompany.pets.model.classes.Identifier;
import com.mycompany.pets.model.classes.superclasses.Animal;
import com.mycompany.pets.model.persistence.CRUD;
import com.mycompany.pets.model.persistence.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControllerAnimal {

    //Crear el metodo para insertar un animal con sus caracteristicas y el identificador en la base de datos
    public static boolean insertAnimal(Animal animal) {
        // Establecer conexión
        CRUD.setConnection(DBConnection.connectionDB());

        // Consulta para insertar en Pets
        String insertAnimalSQL = """
        INSERT INTO Pets (name, dateBirth, sex, weight, conditions, allergies, isAvailable, urlPhoto, IDTypeSpecies, IDOwner)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        // Consulta para insertar en PetsFeaturesPets
        String insertCharacteristicSQL = """
        INSERT INTO PetsFeaturesPets (IDPet, IDFeaturePet, value)
        VALUES (?, ?, ?)
    """;

        // Consulta para insertar en Identifiers (si aplica)
        String insertIdentifierSQL = """
        INSERT INTO Identifiers (number, IDPet, IDTypeIdentifier)
        VALUES (?, ?, ?)
    """;

        try {
            // Deshabilitar auto-commit para manejo de transacciones
            if (CRUD.setAutoCommitDB(false)) {
                // Crear lista de parámetros para insertar el animal
                List<Object> animalParams = List.of(
                        animal.getName(),
                        animal.getBirthDate(),
                        animal.getSex(),
                        animal.getWeight(),
                        animal.getConditions(),
                        animal.getAllergies(),
                        animal.getIsAvailable(),
                        animal.getUrlPhoto(),
                        animal.getIDSpecie(),
                        animal.getOwner()
                );

                // Insertar el animal en Pets
                if (CRUD.insertDB(insertAnimalSQL, animalParams)) {
                    // Obtener el último ID generado (IDPet)
                    ResultSet rs = CRUD.consultDB("SELECT LAST_INSERT_ID()", List.of());
                    if (rs != null && rs.next()) {
                        int animalID = rs.getInt(1);

                        // Insertar características del animal en PetsFeaturesPets
                        for (Characteristic characteristic : animal.getCharacteristics()) {
                            // Obtener IDFeaturePet desde la base de datos
                            String findFeatureSQL = "SELECT IDFeaturePet FROM FeaturesPets WHERE feature = ?";
                            ResultSet featureRS = CRUD.consultDB(findFeatureSQL, List.of(characteristic.getName()));
                            if (featureRS != null && featureRS.next()) {
                                int featureID = featureRS.getInt("IDFeaturePet");

                                // Insertar característica
                                List<Object> characteristicParams = List.of(
                                        animalID,
                                        featureID,
                                        characteristic.getValue()
                                );
                                if (!CRUD.insertDB(insertCharacteristicSQL, characteristicParams)) {
                                    throw new SQLException("Error al insertar característica: " + characteristic.getName());
                                }
                            } else {
                                throw new SQLException("Característica no encontrada: " + characteristic.getName());
                            }
                        }

                        // Si el animal tiene un identificador, insertarlo en Identifiers
                        if (animal.getIdentifier() != null) {
                            List<Object> identifierParams = List.of(
                                    animal.getIdentifier().getNumber(),
                                    animalID,
                                    animal.getIdentifier().getIdTypeIdentifier()
                            );
                            if (!CRUD.insertDB(insertIdentifierSQL, identifierParams)) {
                                throw new SQLException("Error al insertar identificador del animal.");
                            }
                        }

                        // Confirmar transacción
                        CRUD.commitDB();
                        return true;
                    } else {
                        throw new SQLException("Error al obtener el ID del animal recién insertado.");
                    }
                } else {
                    throw new SQLException("Error al insertar el animal en la base de datos.");
                }
            } else {
                return false; // No se pudo desactivar auto-commit
            }
        } catch (SQLException e) {
            // Revertir cambios en caso de error
            CRUD.rollbackDB();
            System.out.println("Error insertando el animal: " + e.getMessage());
            return false;
        } finally {
            // Cerrar la conexión
            CRUD.closeCon();
        }
    }

    private Identifier getIdentifierForAnimal(int idPet) throws SQLException {
        String selectIdentifierSQL = "SELECT number, idTypeIdentifier, idPet FROM Identifiers WHERE idPet = ?";
        List<Object> parameters = new ArrayList<>();
        parameters.add(idPet); // Añadimos el parámetro idPet

        try {
            ResultSet rs = CRUD.consultDB(selectIdentifierSQL, parameters);
            if (rs != null && rs.next()) {
                // Creamos el objeto Identifier usando los valores obtenidos de la base de datos
                String number = rs.getString("number");
                int idTypeIdentifier = rs.getInt("idTypeIdentifier");
                return new Identifier(number, idTypeIdentifier, idPet);
            } else {
                return null; // Si no se encuentra un identificador, devolvemos null
            }
        } catch (SQLException ex) {
            System.out.println("Error al obtener el identificador para el animal: " + ex.getMessage());
            throw ex; // Relanzamos la excepción para que pueda manejarse en niveles superiores si es necesario
        }
    }

    private Animal createAnimalBySpeciesId(int speciesId, ResultSet rsAnimals) throws SQLException {
        int idPet = rsAnimals.getInt("IDPet");

        // Obtener el identifier desde la tabla Identifier
        Identifier identifier = getIdentifierForAnimal(idPet);

        switch (speciesId) {
            case 1: // Especie: Perro
                return new Dog(
                        rsAnimals.getInt("IDPet"),
                        rsAnimals.getString("name"),
                        rsAnimals.getString("dateBirth"),
                        rsAnimals.getString("sex"),
                        rsAnimals.getDouble("weight"),
                        rsAnimals.getString("conditions"),
                        rsAnimals.getString("allergies"),
                        rsAnimals.getString("isAvailable"),
                        rsAnimals.getString("urlPhoto"),
                        rsAnimals.getInt("IDOwner"),
                        identifier
                );
            case 2: // Especie: Gato
                return new Cat(
                        rsAnimals.getString("name"),
                        rsAnimals.getString("dateBirth"),
                        rsAnimals.getString("sex"),
                        rsAnimals.getDouble("weight"),
                        rsAnimals.getString("conditions"),
                        rsAnimals.getString("allergies"),
                        rsAnimals.getString("isAvailable"),
                        rsAnimals.getString("urlPhoto"),
                        rsAnimals.getInt("IDOwner"),
                        identifier
                );
            default:
                throw new IllegalArgumentException("ID de especie desconocido: " + speciesId);
        }
    }

    public List<Animal> listAnimals() throws SQLException {
        CRUD.setConnection(DBConnection.connectionDB());
        List<Animal> animals = new ArrayList<>();

        // Consulta para obtener los animales
        String sqlAnimals = "SELECT * FROM Pets";
        List<Object> parametersAnimals = new ArrayList<>();

        // Consulta para las características
        String sqlCharacteristics
                = "SELECT f.feature, pf.value "
                + "FROM FeaturesPets f "
                + "JOIN PetsFeaturesPets pf ON f.IDFeaturePet = pf.IDFeaturePet "
                + "WHERE pf.IDPet = ?";

        try {
            ResultSet rsAnimals = CRUD.consultDB(sqlAnimals, parametersAnimals);
            while (rsAnimals != null && rsAnimals.next()) {
                int speciesId = rsAnimals.getInt("IDTypeSpecies");
                int animalId = rsAnimals.getInt("IDPet");

                // Crear el objeto animal basado en la especie
                Animal animal = createAnimalBySpeciesId(speciesId, rsAnimals);

                // Limpiar las características previas
                animal.getCharacteristics().clear();

                // Obtener características del animal
                List<Object> parametersCharacteristics = new ArrayList<>();
                parametersCharacteristics.add(animalId);
                ResultSet rsCharacteristics = CRUD.consultDB(sqlCharacteristics, parametersCharacteristics);
                while (rsCharacteristics != null && rsCharacteristics.next()) {
                    String featureName = rsCharacteristics.getString("feature");
                    String featureValue = rsCharacteristics.getString("value");
                    animal.getCharacteristics().add(new Characteristic(featureName, featureValue));
                }

                // Agregar el animal a la lista
                animals.add(animal);
            }
        } catch (SQLException ex) {
            System.out.println("Error al listar los animales: " + ex.getMessage());
        } finally {
            CRUD.closeCon();
        }

        return animals;
    }

}
