package main.java.model;

import java.io.Serializable;
import java.util.ArrayList;

class SellerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    SellerDAO(){
        this.sellersList = new ArrayList<Seller>();
        this.idCurrentSeller = 0;
    }

    public void add_funds(double funds, int indexCustomer){
        this.customersList.get(indexCustomer).add_funds(funds);
    }

    public void add_seller(Seller seller){
        seller.id = this.idCurrentSeller;
        this.idCurrentSeller++;
        this.sellersList.add(seller);        
    }
    
}