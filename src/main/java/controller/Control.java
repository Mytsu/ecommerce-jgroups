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
    public int add_customer(String id, String fullname, String password){

        // Verifica se o cliente existe
        if(this.customers.exists(customer))
            return -1;
    
        customer = Customer(id, fullname, password);
        customer.funds = INITIALFUNDING;
        this.customers.add(customer);        
        return 0
    }

    // Funcao feita para busca de produtos
    public ArrayList<Product> search_product(String string){
        
        lista = new ArrayList<Product>();

        for (Entry<String, Product> entry : this.products.get_products().entrySet()) {
            String key = entry.getKey();
            if(key.contains(string)){
                Product prod = entry.getValue();
                lista.add(prod);
            }
        }
        return lista;
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

    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte dos vendedores                                //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    // Function to add a seller
    public int add_seller(String id, String fullname, String password){
        
        // Verifica se o vendedor existe
        if(this.sellers.exists(id))
            return -1;
        
        seller = Seller(id, fullname, password);
        this.sellers.add(seller);        

        return 0;
    }

    public int add_product(String idSeller, String product, Float price, long amount, String description){
        //Checa se a quantidade e maior que 0
        if(amount <= 0)
            return -1

        //Checa se o preco e maior que 0
        if(price <= 0)
            return -2

        // Verifica se o vendedor existe
        if(!this.sellers.exists(idSeller))
            return -3;

        // Verifica se o produto nao esta no hashmap
        if(!this.products.exists(product))
            products.add_product(new Produto(product, description));

        //Decidir se vai incrementar caso o produto e o vendedor ja exista
        //Ou se simplesmente vai criar uma nova oferta
        products.get_product(product).add_offer(new Offer(idSeller, price, amount));

        return 0
    }

    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte do sistema                                    //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    public double get_total_founds(){
        
        soma = 0.0;
        for (Entry<String, Customer> entry : this.customers.get_customers.entrySet()) {
            Customer custom = entry.getValue();
            soma += custom.get_founds();
        }
        
        for (Entry<String, Customer> entry : this.sellers.get_sellers.entrySet()) {
            Customer seller = entry.getValue();
            soma += seller.get_founds();
        }

        return soma;
    }

    public boolean is_founds_right(){
        if( this.customers.num_customers*1000 == this.get_total_founds())
            return true;
        return false;
    }

}