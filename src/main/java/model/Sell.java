package model;

import java.io.Serializable;

public class Sell implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    public String sellerId, customerId, productId;
    public double price;
    public long quant;

    public Sell(String sellerId, String customerId, String productId, double price, long quant) {
        this.sellerId = sellerId;
        this.customerId = customerId;
        this.productId = productId;
        this.price = price;
        this.quant = quant;
    }
}