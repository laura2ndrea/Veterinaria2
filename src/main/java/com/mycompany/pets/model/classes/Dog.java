package com.mycompany.pets.model.classes;
import com.mycompany.pets.model.classes.superclasses.Animal;

public class Dog extends Animal {

    public Dog(int id, String name, String birthDate, String sex, double weight, String conditions, String allergies, String isAvailable, String urlPhoto, int owner, Identifier identifier) {
        super(id, name, birthDate, sex, weight, conditions, allergies, isAvailable, urlPhoto, owner, identifier);
        setIDSpecie(1);
    }
    
    public Dog(){
        super(); 
        setIDSpecie(1);
        setUrlPhoto("Perro.jpg");
    }
    
    @Override
    protected void initializeCharacteristics() {
        addCharacteristic(new Characteristic("Breed", "None"));
        addCharacteristic(new Characteristic("Color", "None"));
        addCharacteristic(new Characteristic("Type", "None"));
        
    }
}