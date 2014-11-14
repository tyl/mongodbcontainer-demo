package com.example.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Created by evacchi on 14/11/14.
 */
public class Address {
    private String street;
    private String zipCode;
    private String city;
    private String state;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipcode) {
        this.zipCode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


}
