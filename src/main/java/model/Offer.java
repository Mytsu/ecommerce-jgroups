package main.java.model;

import java.io.Serializable;
import java.util.ArrayList;

class Offer implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    Offer(String sellerLogin, String sellerName, float price, int quant){
        this.sellerLogin = sellerLogin;
        this.sellerName = sellerName;
        this.price = price;
        this.quant = quant;
    }
}