package com.example;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

/**
 * Created by evacchi on 10/11/14.
 */
public class Northwind {
    @Id
    private ObjectId id;
    private String ShipName;

    public ObjectId getId() {
        return id;
    }

    public String getShipName() {
        return ShipName;
    }

    public void setShipName(String shipName) {
        ShipName = shipName;
    }
}
