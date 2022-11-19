package com.driver;

public class Order {

    private String id;
    private int deliveryTime;

    public Order(String id, String deliveryTime) {

        // The deliveryTime has to converted from string to int and then stored in the attribute
        //deliveryTime  = HH*60 + MM
        int a = Integer.valueOf(deliveryTime.substring(0,3));
        int b = Integer.valueOf(deliveryTime.substring(4,deliveryTime.length()));

        this.deliveryTime = (a*60) + b;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTime() {return deliveryTime;}
}
