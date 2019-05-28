package model;

import java.io.Serializable;

class Offer implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    private String sellerId;
    private double price;
    private long amount;

    Offer(String sellerId, double price, long amount) {
        this.sellerId = sellerId;
        this.price = price;
        this.amount = amount;
    }

    public long get_amount(){
        return this.amount;
    }

    
}