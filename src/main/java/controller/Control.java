package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;

import model.*;

public class Control implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    private static final double INITIALFUNDING = 1000.0;

    public CustomerDAO customers;
    public SellerDAO sellers;
    public ProductDAO products;
    
    Control(){
        this.customers = new CustomerDAO();
        this.sellers = new SellerDAO();
        this.products = new ProductDAO();
    }
    
    // Function to add a customer
    public int add_customer(String id, String fullname, String password){

        Customer customer = new Customer(id, fullname, password);

        // Verifica se o cliente existe
        if(this.customers.exists(id))
            return -1;
    

        customer.funds = INITIALFUNDING;
        this.customers.add_customer(customer);        
        return 0;
    }

    public int login_customer(String customer, String password){
        // Verifica se o cliente existe
        if(!this.customers.exists(customer))
            return -1;
        if(this.customers.get_customer(customer).password != password)
            return -2;
        
        return 0;
    }

    // Funcao feita para busca de produtos
    public ArrayList<Product> search_product(String string){
        
        ArrayList<Product> lista = new ArrayList<Product>();

        for (Entry<String, Product> entry : this.products.get_products().entrySet()) {
            String key = entry.getKey();
            if(key.contains(string)){
                Product prod = entry.getValue();
                lista.add(prod);
            }
        }
        return lista;
    }

    public int purchase(String customer, String seller, String product, int amount){

        // Verifica se o produto está no hashmap
        if(!this.products.exists(product)) {
            return -1;
        }

        // Verifica se o cliente existe
        if(!this.customers.exists(customer)) {
            return -2;
        }

        // Verifica se o vendedor existe
        if(!this.sellers.exists(seller)) {
            return -3;
        }

        // Verifica se a quantidade não é negativa
        if(amount <= 0) {
            return -4;
        }

        // Verifica se tem produtos o suficiente do vendedor X
        if(!this.products.get_product(product).has_enougth(seller, amount)) {
            return -5;
        }

        // Pega o preço do produto desta venda referente ao vendedor escolhido pelo cliente
        double price = this.products.get_product(product).get_price(seller);

        // Verifica se o cliente tem dinheiro o suficiente para comprar
        if(! (price > this.customers.get_customer(customer).get_funds())) {
            return -6;
        }

        // Incrementa o valor nos fundos do vendedor
        this.sellers.add_funds(price*amount, this.sellers.get_seller(seller));   
        // Deduz o valor nos fundos do cliente
        this.customers.add_funds(-price*amount, customer);

        // Deduz a quantidade do produto X do vendedor Y
        this.products.get_product(product).deduce_amount(seller, amount);
        Sell sell = new Sell(seller, customer, product, price, amount);
        this.customers.get_customer(customer).add_sell(sell);
        this.sellers.get_seller(seller).add_sell(sell);

        return 0;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte dos vendedores                                //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    // Function to add a seller
    public int add_seller(String id, String fullname, String password) {
        
        // Verifica se o vendedor existe
        if(this.sellers.exists(id))
            return -1;
        
        Seller seller = new Seller(id, fullname, password);
        this.sellers.add_seller(seller);

        return 0;
    }

    public int login_seller(String seller, String password) {
        // Verifica se o cliente existe
        if(!this.sellers.exists(seller)) {
            return -1;
        }

        if(this.sellers.get_seller(seller).password != password) {
            return -2;
        }
        
        return 0;
    }

    public int add_product(String idSeller, String product, Float price, long amount, String description) {
        //Checa se a quantidade e maior que 0
        if(amount <= 0) {
            return -1;
        }

        //Checa se o preco e maior que 0
        if(price <= 0) {
            return -2;
        }

        // Verifica se o vendedor existe
        if(!this.sellers.exists(idSeller)) {
            return -3;
        }

        // Verifica se o produto nao esta no hashmap
        if(!this.products.exists(product))
            products.add_product(new Product(product, description));

        //Decidir se vai incrementar caso o produto e o vendedor ja exista
        //Ou se simplesmente vai criar uma nova oferta
        products.get_product(product).add_offer(new Offer(idSeller, price, amount));

        return 0;
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte do sistema                                    //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public double get_total_founds() {
        
        double soma = 0.0;
        for (Entry<String, Customer> entry : this.customers.get_customers().entrySet()) {
            Customer custom = entry.getValue();
            soma += custom.get_funds();
        }
        
        for (Entry<String, Seller> entry : this.sellers.get_sellers().entrySet()) {
            Seller seller = entry.getValue();
            soma += seller.get_funds();
        }

        return soma;
    }

    public boolean is_founds_right(){
        if( this.customers.num_customers*1000 == this.get_total_founds())
            return true;
        return false;
    }

}