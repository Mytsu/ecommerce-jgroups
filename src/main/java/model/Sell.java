package model;

import java.io.Serializable;

class Sell implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    final String id;
    private String sellerId, customerId, productId;
    private double price;
    private long quant;

    Sell(String sellerId, String customerId, String productId, double price, long quant, String id) {
        this.id = id;
        this.sellerId = sellerId;
        this.customerId = customerId;
        this.productId = productId;
        this.price = price;
        this.quant = quant;
    }
}