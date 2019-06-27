package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import system.*;

public class Persistence extends ReceiverAdapter implements RequestHandler, Serializable{

    private static final long serialVersionUID = 4506509784967298618L;
	
    private CustomerDAO customers;
    private SellerDAO sellers;
    private ProductDAO products;
    
    private JChannel control_modelChannel ;
    private MessageDispatcher control_modelDispatcher;
    
    public Persistence() throws Exception{
    	
        this.customers = new CustomerDAO();
        this.sellers = new SellerDAO();
        this.products = new ProductDAO();
        
        try {
			this.control_modelChannel = new JChannel("control_model.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        this.control_modelChannel.setReceiver(this);
        
        control_modelDispatcher = new MessageDispatcher(this.control_modelChannel, null, null, this);
        
        // Tive que colocar o throws para nao ter que dar try catch abaixo
        this.control_modelChannel.connect("ControlModelChannel");
        System.out.println("Passei 1");
		this.montaGrupo();
		System.out.println("Passei 2");
    }
    
    private void montaGrupo() {
        
        // Informa para todos os membros do controle que há um novo modelo no pedaço e não anota nenhum endereço
    	RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_NONE);
        options.setAnycasting(false);
        
        Comunication comunication = new Comunication(EnumChannel.MODEL_TO_CONTROL, EnumServices.NEW_MODEL_MEMBER, null);
        Address cluster = null;
        Message newMessage = new Message(cluster, comunication);
        
        try {
			this.control_modelDispatcher.castMessage(null, newMessage, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return;
    }
       
    public void receive(Message msg) { //exibe mensagens recebidas
    	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem informativa
    		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
    		Message poderia ser um registro contendo vários campos, para facilitar
    	*/

    		// DEBUG: neste exemplo, iremos apenas imprimir a mensagem que chegou
            System.out.println("" + msg.getSrc() + ": " + msg.getObject()+"(1)");
        }
        
    private List<Product> getItens() {
    	/*	OLD CODE
    	ArrayList<Product> lista = new ArrayList<Product>();
        for (Entry<String, Product> entry : this.products.get_products().entrySet()) {
        	lista.add(entry.getValue());
        }
        
        return lista;
    	 */
    	return products.get_products();
    }
    
    private List<Customer> getCustomers(){
    	return customers.get_customers();
    }
    
    private List<Seller> getSellers() {
    	return sellers.get_sellers();
    }
    
    private boolean customerExist(String id) {
    	if(customers.exists(id)) {
    		return true;
    	}
    	return false;
    }

    private boolean sellerExist(String id) {
    	if(sellers.exists(id)) {
    		return true;
    	}
    	return false;
    }
    
    private boolean itemExist(String id) {
    	if(products.exists(id)) {
    		return true;
    	}
    	return false;
	}
	
	private boolean questionExist(String product, String question){
		if(products.get_product(product).questions.containsKey(question)){
			return true;
		}
		return false;
	}
    
    private boolean addCustomer(Customer customer) {
    	this.customers.add_customer(customer);
    	return true;
    }
    
    private boolean addSeller(Seller seller) {
    	this.sellers.add_seller(seller);
    	return true;
    }
    
    private boolean add_product(String idSeller, String product, Float price, long amount, String description) {
       
    	// Verifica se o produto nao esta no hashmap
        if(!this.products.exists(product))
            products.add_product(new Product(product, description));

        //Decidir se vai incrementar caso o produto e o vendedor ja exista
        //Ou se simplesmente vai criar uma nova oferta
        products.get_product(product).add_offer(new Offer(idSeller, price, amount));
    	
        return true;
    }
    
    private boolean confirmLoginCustomer(String customer, String password) {
    	if(!this.customers.exists(customer))
            return false;
        if(!this.customers.get_customer(customer).password.equals(password))
            return false;
        return true;
    }
    
    private boolean confirmLoginSeller(String seller, String password) {
    	if(!this.sellers.exists(seller))
            return false;
        if(this.sellers.get_seller(seller).password != password)
            return false;
        return true;
    }
    
    private List<Product> search_product(String string){
		List<Product> products = this.products.get_products();
		List<Product> out = new ArrayList<Product>();
		for (Product p : products) {
			if (p.id.equals(string) || p.description.equals(string)) {
				out.add(p);
			}
		}
		return out;
    }
    
    private int possibleMakePurchase(String customer, String seller, String product, int amount) {
      
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
        
        return 0;
    }
    
    private boolean makePurchase(String customer, String seller, String product, int amount) {
    	
    	double price = this.products.get_product(product).get_price(seller);

        // Incrementa o valor nos fundos do vendedor
        this.sellers.add_funds(price*amount, seller);   
        // Deduz o valor nos fundos do cliente
        this.customers.add_funds(-price*amount, customer);

        // Deduz a quantidade do produto X do vendedor Y
        this.products.get_product(product).deduce_amount(seller, amount);
        Sell sell = new Sell(seller, customer, product, price, amount);
        this.customers.get_customer(customer).add_sell(sell);
        this.sellers.get_seller(seller).add_sell(sell);
        
        return true;
    }
    
    private boolean saveQuestion(Question question, String product) {
        products.get_product(product).add_question(question);
    	return true;
    }
	
	private boolean saveAnswer(String product, String question, String seller, String answer) {
		
		Answer answer2 = new Answer(seller, answer);
		this.products.get_product(product).questions.get(question).add_answer(answer2);
		
		return true;
	}
		
    private ArrayList<Sell> getBougthItens(String customer) {
    	return this.customers.get_customer(customer).sell;
    }
    
    private ArrayList<Sell> getSoldItens(String seller) {
    	return this.sellers.get_seller(seller).sell;
    }
    
    private double getTotalFunds() {
		double soma = 0.0;
		List<Customer> customers = this.getCustomers();
		List<Seller> sellers = this.getSellers();
		for (Customer c : customers) {
			soma += c.get_funds();
		}
        
        for (Seller s : sellers) {
			soma += s.get_funds();
		}
        return soma;
    }
    
    private boolean isFundsRight() {
        if( this.customers.num_customers*1000 == this.getTotalFunds())
            return true;
        return false;
	}
    
    
    // responde requisições recebidas
    @Override
    public Object handle(Message message) { //throws Exception {
    	
    	System.out.println("Chegou a mensagem pra persistencia: " + message.getObject());
    	Comunication msg = (Comunication)message.getObject();    	
    	
        
    	Comunication response = new Comunication();
    		
    	ArrayList<Object> content = new ArrayList<Object>();
    	
    	if(msg.channel == EnumChannel.CONTROL_TO_MODEL) {
    		
    		//	Retorna todos os os itens existentes.
    		if(msg.service == EnumServices.GET_ITENS) {	
    			response.service = EnumServices.GET_ITENS;
    			List<Product> var = this.getItens();
    			content.add(var);
    		}
    		
    		//	Retorna todos os os clientes existentes
    		else if(msg.service == EnumServices.GET_CUSTOMERS) {
    			response.service = EnumServices.GET_CUSTOMERS;
    			List<Customer> var = this.getCustomers();
    			content.add(var);
    		}
    		
    		//	Retorna todos os os vendedores existentes
    		else if(msg.service == EnumServices.GET_SELLERS) {
    			response.service = EnumServices.GET_SELLERS;
    			List<Seller> var = this.getSellers();
    			content.add(var);
    		}
    		
    		//	Resposta para quando um membro da controle manda um multicast avisando que é novo
    		//dai todos os membros do modelo respondem para que ele adicione todos no seu vetor de endereços
    		else if(msg.service == EnumServices.NEW_CONTROL_MEMBER) {
    			response.service = EnumServices.NEW_CONTROL_MEMBER;
    			content = null;
    		}
    		
    		else if(msg.service == EnumServices.CUSTOMER_EXIST) {
    			//	boolean customerExist(String id)
    			response.service = EnumServices.CUSTOMER_EXIST;
    			boolean var = this.customerExist((String)msg.content.get(0));
    			content.add(var);
    		}

    		else if(msg.service == EnumServices.SELLER_EXIST) {
    			//	boolean sellerExist(String id)
    			response.service = EnumServices.SELLER_EXIST;
    			boolean var = this.sellerExist((String)msg.content.get(0));
    			content.add(var);
    		}

    		else if(msg.service == EnumServices.ITEM_EXIST) {
    			//	boolean itemExist(String id)
    			response.service = EnumServices.ITEM_EXIST;
    			boolean var = this.itemExist((String)msg.content.get(0));
    			content.add(var);
			}
			
			else if(msg.service == EnumServices.QUESTION_EXIST) {
    			//	boolean questionExist(String product, String question)
    			response.service = EnumServices.QUESTION_EXIST;
    			boolean var = this.questionExist((String)msg.content.get(0), (String)msg.content.get(1));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.SAVE_CUSTOMER) {
				//	boolean addCustomer(Customer cus)
				response.service = EnumServices.SAVE_CUSTOMER;
				//content.add(true);
    			boolean var = this.addCustomer((Customer)msg.content.get(0));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.SAVE_SELLER) {
    			//	boolean addCustomer(Customer cus)
    			response.service = EnumServices.SAVE_SELLER;
    			boolean var = this.addSeller((Seller)msg.content.get(0));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.SAVE_ITEM) {
    			//	boolean add_product(String idSeller, String product, Float price, long amount, String description)
    			response.service = EnumServices.SAVE_ITEM;
    			boolean var = this.add_product((String)msg.content.get(0), (String)msg.content.get(1),
    					(float)msg.content.get(2), (long)msg.content.get(3), (String)msg.content.get(4));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.CONFIRM_LOGIN_CUSTOMER) {
    			//	boolean confirmLoginCustomer(String customer, String password)
    			response.service = EnumServices.CONFIRM_LOGIN_CUSTOMER;
    			boolean var = this.confirmLoginCustomer((String)msg.content.get(0),(String)msg.content.get(1));
				content.add(var);
    		}

    		
    		else if(msg.service == EnumServices.CONFIRM_LOGIN_SELLER) {
    			//	boolean confirmLoginSeller(String seller, String password)
    			response.service = EnumServices.CONFIRM_LOGIN_SELLER;
    			boolean var = this.confirmLoginSeller((String)msg.content.get(0),(String)msg.content.get(1));
    			content.add(var);
    		}
    		

    		else if(msg.service == EnumServices.MAKE_SEARCH_ITEM) {
        		//	ArrayList<Product> search_product(String string)
    			response.service = EnumServices.MAKE_SEARCH_ITEM;
    			List<Product> var = this.search_product((String) msg.content.get(0));
    			content.add(var);
    		}

    		else if(msg.service == EnumServices.POSSIBLE_MAKE_PURCHASE) {
        		//	int possibleMakePurchase(String customer, String seller, String product, int amount)
    			response.service = EnumServices.POSSIBLE_MAKE_PURCHASE;
    			int var = this.possibleMakePurchase((String)msg.content.get(0),
    					(String)msg.content.get(1),(String)msg.content.get(2),(int)msg.content.get(3));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.MAKE_PURCHASE) {
        		//	boolean makePurchase(String customer, String seller, String product, int amount)
    			response.service = EnumServices.MAKE_PURCHASE;
    			boolean var = this.makePurchase((String)msg.content.get(0),
    					(String)msg.content.get(1),(String)msg.content.get(2),(int)msg.content.get(3));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.SAVE_QUESTION) {
    			//	boolean saveQuestion(Question question, String product)
    			response.service = EnumServices.SAVE_QUESTION;
    			boolean var = this.saveQuestion((Question)msg.content.get(0), (String)msg.content.get(1));
    			content.add(var);
			}
			
    		else if(msg.service == EnumServices.SAVE_ANSWER) {
    			//	boolean saveAnswer(String product, String question, String seller, String answer)
    			response.service = EnumServices.SAVE_QUESTION;
				boolean var = this.saveAnswer((String)msg.content.get(0), (String)msg.content.get(1),
				(String)msg.content.get(2), (String)msg.content.get(3));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.GET_BOUGHT_ITENS) {
    			//	ArrayList<Sell> getBougthItens(String customer)
    			response.service = EnumServices.GET_BOUGHT_ITENS;
    			ArrayList<Sell> var = this.getBougthItens((String)msg.content.get(0));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.GET_SOLD_ITENS) {
    			//	ArrayList<Sell> getSoldItens(String seller)
    			response.service = EnumServices.GET_SOLD_ITENS;
    			ArrayList<Sell> var = this.getSoldItens((String)msg.content.get(0));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.TOTAL_FUNDS_INT) {
    			//	double get_total_founds()
    			response.service = EnumServices.TOTAL_FUNDS_INT;
    			double var = this.getTotalFunds();
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.TOTAL_FUNDS_BOOL) {
    			//	boolean is_founds_right()
    			response.service = EnumServices.TOTAL_FUNDS_BOOL;
    			boolean var = this.isFundsRight();
    			content.add(var);
    		}

    		response.channel = EnumChannel.MODEL_TO_CONTROL;
    		response.content = content;
    	}
    	
    	
    	else if (msg.channel == EnumChannel.MODEL_TO_CONTROL) {
    		// Mensagem de quando um membro do modelo manda para o grupo do controle falando que ele existe
    		//a mensagem vai acabar chegando para membros do modelo que meio que ignoram a mesma
    		if(msg.service == EnumServices.NEW_MODEL_MEMBER) {
    			response.channel = EnumChannel.MODEL_TO_MODEL;
    			response.service = EnumServices.NEW_MODEL_MEMBER;
    			response.content = null;
    		}
    	}

    	  //DEBUG: neste exemplo, a Message contém apenas uma String 
          // contendo uma pergunta qualquer. 
          //String pergunta = (String) msg.getObject();
          //System.out.println("RECEBI uma mensagem: " + pergunta+"\n");
          //User usuario = new User("UserNameQQ1","NomeCompletoQQ1","PassWordQQ1",122.541);
          //if(pergunta.contains("concorda"))
          //    return pergunta;
            //return "SIM (1)"; //resposta à requisição contida na mensagem
          //else
          //  return " NÃO (1)";
		
			  
		System.out.println(response);
        
        return response;
	}



		
}
