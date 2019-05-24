package main.java.model;

import java.io.Serializable;
import java.util.ArrayList;

class CustomerDAO implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    static double INITIALFUNDING = 1000.0;
    final double TOTALFUNDINGINSYSTEM = 0;

    CustomerDAO(){
        this.customersList = new ArrayList<Customer>();
        this.idCurrentCustomer = 0;
    }

    public void add_customer(Customer customer){
        customer.id = this.idCurrentCustomer;
        customer.funds = INITIALFUNDING;
        TOTALFUNDINGINSYSTEM += INITIALFUNDING;
        this.idCurrentCustomer++;
        this.customersList.add(customer);        
    }

    public void add_funds(double funds, int indexCustomer){
        this.customersList.get(indexCustomer).add_funds(funds);
    }
}