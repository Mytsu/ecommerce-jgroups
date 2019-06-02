package system;

import java.io.Serializable;

public class Offer implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    public String id;
    public double price;
    public long amount;

    public Offer(String id, double price, long amount) {
        this.id = id;
        this.price = price;
        this.amount = amount;
    }

    public long get_amount(){
        return this.amount;
    }

    
}