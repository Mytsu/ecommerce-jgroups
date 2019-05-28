package main.java.controller;

import java.io.Serializable;
import java.util.ArrayList;

import sun.tools.tree.ThisExpression;

class Control implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    
    Control(){
        this.customers = CustomerDAO();
        this.sellers = SellerDAO();
        this.products = ProductDAO();
    }
    
    // Function to add a customer
    public void add_customer(String id, String fullname, String password){
        customer = Customer(id, fullname, password);
        //customer.id = this.idCurrentCustomer;
        customer.funds = INITIALFUNDING;
        TOTALFUNDINGINSYSTEM += INITIALFUNDING;
        this.customers.add(customer);        
    }

    //
    public ArrayList<String> search_product(String string){
        

    }

    public int purchase(String client, String seller, String product, int amount){

        // Verifica se o produto está no hashmap
        if(!this.products.exists(product))
            return -1;

        // Verifica se o cliente existe
        if(!this.customers.exists(customer))
            return -2;

        // Verifica se o vendedor existe
        if(!this.sellers.exists(seller))
            return -3;

        // Verifica se a quantidade não é negativa
        if(amount <= 0)
            return -4;

        // Verifica se tem produtos o suficiente do vendedor X
        if(!this.products.get_product(product).has_enougth(seller, amount))
            return -5;

        // Pega o preço do produto desta venda referente ao vendedor escolhido pelo cliente
        price = this.products.get_product(product).get_price(seller);

        // Verifica se o cliente tem dinheiro o suficiente para comprar
        if(! (price > this.customers.get_customer(client).get_founds()))
            return -6;


        // Incrementa o valor nos fundos do vendedor
        this.sellers.add_funds(price*amount, this.sellers.get_seller(seller));   
        // Deduz o valor nos fundos do cliente
        this.customers.add_funds(-price*amount, this.customers.get_customer(customer));

        // Deduz a quantidade do produto X do vendedor Y
        this.products.get_product(product).deduce_amount(seller, amount);
        Sell(String sellerId, String customerId, String productId, double price, long quant) 
        sell = Sell(seller, customer, product, price, amount);
        this.customers.get_customer(customer).add_sell(sell);
        this.sellers.get_seller(seller).add_sell(sell);

        return 0;      
    }



    // Function to add a seller
    public void add_seller(String id, String fullname, String password){
        seller = Seller(id, fullname, password);
        //seller.id = this.idCurrentSeller;
        this.sellers.add(seller);        
    }

    public void add_funds(double funds, int indexCustomer){
        this.customersList.get(indexCustomer).add_funds(funds);
    }


}