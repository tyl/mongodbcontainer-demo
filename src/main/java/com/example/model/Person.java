package com.example.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Created by evacchi on 07/11/14.
 */
public class Person {
    @Id
    private ObjectId id;
    private String firstName;
    private String lastName;
    private Address address;

    public Person() {
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public ObjectId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address == null ?
                (address = new Address())
                : address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}