package model;

import java.io.Serializable;

class Offer implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    final String id;
    private String sellerId;
    private double price;
    private long amount;

    Offer(String id, String sellerId, double price, long amount) {
        this.id = id;
        this.sellerId = sellerId;
        this.price = price;
        this.amount = amount;
    }
}