package main.java.model;

import java.io.Serializable;

class Sell implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    Sell(String userSeller, String userCustomer, String product, float price, int quant, int id){
        this.userSeller = userSeller;
        this.userCustomer = userCustomer;
        this.product = product;
        this.price = price;
        this.quant = quant;
        this.id = id;
    }
}