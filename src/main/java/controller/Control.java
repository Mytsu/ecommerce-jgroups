package controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import org.jgroups.*;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import system.*;


public class Control extends ReceiverAdapter implements RequestHandler, Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    private static final double INITIALFUNDING = 1000.0;

    
    public JChannel view_controlChannel;
    public JChannel control_modelChannel;
    
    private MessageDispatcher control_viewDispatcher;
    private MessageDispatcher control_modelDispatcher;
    
    private Vector<Address> enderecosModelo;
    
    public Control() throws Exception {

        enderecosModelo = new Vector<Address>();
        
        try {
			this.view_controlChannel = new JChannel("generalXML.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        try {
			this.control_modelChannel = new JChannel("generalXML.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
        //this.control_modelChannel = new JChannel("control_model.xml");
        
        this.view_controlChannel.setReceiver(this);
        this.control_modelChannel.setReceiver(this);
        
        control_viewDispatcher = new MessageDispatcher(this.view_controlChannel, null, null, this);
        control_modelDispatcher = new MessageDispatcher(this.control_modelChannel, null, null, this);
        
        // Tive que colocar o throws para nao ter que dar try catch abaixo
        this.view_controlChannel.connect("ViewControlChannel");
        this.control_modelChannel.connect("ControlModelChannel");
        
        this.montaGrupo();
    }
    
    private void montaGrupo() {
    	
    	// Organiza todas as informações para mandar uma mensagem e obter todos os endereços do modelo
    	RequestOptions options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(false);
        
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.NEW_CONTROL_MEMBER, null);
        Address cluster = null;
        Message newMessage = new Message(cluster, comunication);

        RspList<Comunication> list = null;
		try {
			list = control_modelDispatcher.castMessage(null, newMessage, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        for(Rsp<Comunication> x : list) {
            if(x.getValue()!=null && x.getValue().channel == EnumChannel.MODEL_TO_CONTROL)
                enderecosModelo.add(x.getSender());
        }
        
        
        // Informa para todos os membros da visão que há um novo controle no pedaço e não anota nenhum endereço
        options = null;
        options = new RequestOptions();
        options.setMode(ResponseMode.GET_NONE);
        options.setAnycasting(false);
        
        comunication = null;
        comunication =  new Comunication(EnumChannel.CONTROL_TO_VIEW, EnumServices.NEW_CONTROL_MEMBER, null);
        cluster = null;
        newMessage = null;
        newMessage = new Message(cluster, comunication);
        
        try {
			this.control_viewDispatcher.castMessage(null, newMessage, options);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    	return;
    }
  
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte dos clientes                                  //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    // Function to add a customer
    private boolean add_customer(String id, String fullname, String password) {

        // Verifica se o cliente existe
        /* OLD
        if(this.customers.exists(id))
            return false;
        */
        
    	//Checa se já existe esse cliente
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(id);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.CUSTOMER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);

        if((boolean)comunication.content.get(0) == true) {
        	return false;
        }
        
        // Adiciona ele realmente no modelo
        Customer customer = new Customer(id, fullname, password);
        customer.funds = INITIALFUNDING;
        
        ArrayList<Object> content2 = new ArrayList<Object>();
        content2.add(customer);
        //content2.add("customer string");
        //content2.add("Uma outra string pra ver se funfa.");
        //content2.add(new Product("Produto qq","Descricao qq"));

        Comunication comunication2 = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SAVE_CUSTOMER, content2);
        
        
        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //o cliente foi adicionado com o sucesso.
        RspList<Comunication> responses = this.sendMessageModelAll(comunication2);
        //Comunication responses = this.sendMessageModelAny(comunication);
        //this.customers.add_customer(customer); OLD
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}

        return bool;
    }

    private boolean login_customer(String customer, String password) {
        
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(customer);
        content.add(password);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.CONFIRM_LOGIN_CUSTOMER, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (boolean)comunication.content.get(0);
    }

    // Funcao feita para busca de produtos
    @SuppressWarnings("unchecked")
	private ArrayList<Product> search_product(String string) {
        
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(string);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.MAKE_SEARCH_ITEM, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (ArrayList<Product>)comunication.content.get(0);
    }

    private int purchase(String customer, String seller, String product, int amount) {

        //	Verifica se a quantidade não é negativa
        if(amount <= 0) {
            return -4;
        }
    	
        //	Faz a checagem sobre a compra poder ser feita
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(customer);
        content.add(seller);
        content.add(product);
        content.add(amount);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.POSSIBLE_MAKE_PURCHASE, content);
        comunication = this.sendMessageModelAny(comunication);
    	
    	if((int)comunication.content.get(0) != 0) {
    		return (int)comunication.content.get(0);
    	}
        
        

    	//	Efetiva de fato a compra
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.MAKE_PURCHASE, content);
        
        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //a compra com sucesso.
        
        RspList<Comunication> responses = this.sendMessageModelAll(comunication);
        
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}
        
        if(bool) {
        	return 0;
        }
       	//	-7 seria onde deu algum erro na escrita
       	return -7;
    	
    }

    @SuppressWarnings("unchecked")
	private ArrayList<Product> list_products() {
    	
        ArrayList<Object> content = null;
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.GET_ITENS, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (ArrayList<Product>)comunication.content.get(0);  	
    }
    
    private boolean sendQuestion(String customer, String product, String msg) {
    	
    	ArrayList<Object> content = new ArrayList<Object>();
    	content.add(product);
    	Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.ITEM_EXIST, content);

    	//	Checa se o produto existe
    	comunication = this.sendMessageModelAny(comunication);
    	if(!(boolean)comunication.content.get(0))
    		return false;
    	
    	content=null; content = new ArrayList<Object>();
    	content.add(customer);
    	comunication = null; comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.CUSTOMER_EXIST, content);
    	
    	//	Checa se o cliente existe
    	comunication = this.sendMessageModelAny(comunication);
    	if(!(boolean)comunication.content.get(0))
    		return false;
    	
    	
    	//	Manda salvar no modelo a pergunta
    	Question question = new Question(msg, customer);
    	
    	content = null; content = new ArrayList<Object>();
    	content.add(product);
    	content.add(question); 	
    	
        comunication = null;
    	comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SAVE_QUESTION, content);   
    	
        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //a compra com sucesso.
        
        RspList<Comunication> responses = this.sendMessageModelAll(comunication);
        
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}
    	
    	return bool;
    }
    
    @SuppressWarnings("unchecked")
	private ArrayList<Sell> getBougthItens(String customer){
    	
        ArrayList<Object> content = new ArrayList<Object>();
        
        content.add(customer);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.CUSTOMER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        
        //	Caso do cliente não existir retorna o array vazio
        if(!(boolean)comunication.content.get(0) == true) {
        	return null;
        }
    	
        comunication = null; comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
                EnumServices.GET_BOUGHT_ITENS, content);
                
        comunication = this.sendMessageModelAny(comunication);
    	
        return (ArrayList<Sell>) comunication.content.get(0);
    }
    
       
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte dos vendedores                                //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    
    // Function to add a seller
    private boolean add_seller(String id, String fullname, String password) {
        
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(id);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SELLER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        
        if((boolean)comunication.content.get(0) == true) {
        	return false;
        }
        
        // Adiciona ele realmente no modelo
        Seller seller = new Seller(id, fullname, password);
        seller.funds = INITIALFUNDING;
        
        content = null; content = new ArrayList<Object>();
        content.add(seller);
        
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SAVE_SELLER, content);
        
        
        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //o cliente foi adicionado com o sucesso.
        
        RspList<Comunication> responses = this.sendMessageModelAll(comunication);
        //this.customers.add_customer(customer); OLD
        
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}
        
        return bool;
    }

    private boolean login_seller(String seller, String password) {

        ArrayList<Object> content = new ArrayList<Object>();
        content.add(seller);
        content.add(password);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.CONFIRM_LOGIN_SELLER, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (boolean)comunication.content.get(0);
    }

    private int add_product(String idSeller, String product, Float price, long amount, String description) {
        //Checa se a quantidade e maior que 0
        if(amount <= 0) {
            return -1;
        }

        //Checa se o preco e maior que 0
        if(price <= 0) {
            return -2;
        }

        // Verifica se o vendedor existe
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(idSeller);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
        		EnumServices.SELLER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        if(!(boolean)comunication.content.get(0))
        	return -3;
        
        
        //	Salva de fato esse novo item ou oferta
        content.add(product);
        content.add(price);
        content.add(amount);
        content.add(description);
        
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SAVE_ITEM, content);
        
        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //o cliente foi adicionado com o sucesso.
        
        RspList<Comunication> responses = this.sendMessageModelAll(comunication);
        //this.customers.add_customer(customer); OLD
        
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}
        
        if(bool)
        	return 0;
        //	Caso de alguma das escritas tiver dadoe errado
        return -4;
    }

    private boolean sendAnswer(String seller, String product, String question, String answer) {
    	
        // Verifica se o vendedor existe
        ArrayList<Object> content = new ArrayList<Object>();
        content.add(seller);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
                EnumServices.SELLER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        if(!(boolean)comunication.content.get(0))
            return false;

        // Verifica se o produto existe
        content.clear();
        content.add(product);
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.ITEM_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        if(!(boolean)comunication.content.get(0))
            return false;

        // Verifica se a pergunta existe
        content.clear();
        content.add(product);
        content.add(question);
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.QUESTION_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        if(!(boolean)comunication.content.get(0))
            return false;
            

        // Manda salvar de fato a pergunta
        content.add(seller);
        content.add(answer);
        comunication = null;
        comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SAVE_ANSWER, content);

        //	TODO  Abaixo falta colocar em um laço de acordo para todos os modelos falarem que escreveu que
        //o cliente foi adicionado com o sucesso.
        
        RspList<Comunication> responses = this.sendMessageModelAll(comunication);
        //this.customers.add_customer(customer); OLD
        
        boolean bool = true;
        
        for (Rsp<Comunication> rsp : responses) {
			bool = bool & (boolean)rsp.getValue().content.get(0);
		}
        
        if(bool)
        	return true;

        return false;

    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<Sell> getSoldItens(String seller){
    	
        ArrayList<Object> content = new ArrayList<Object>();
        
        content.add(seller);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SELLER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        
        //	Caso do vendedor não existir retorna o nulo
        if(!(boolean)comunication.content.get(0) == true) {
        	return null;
        }
    	
        comunication = null; comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
                EnumServices.GET_SOLD_ITENS, content);
                
        comunication = this.sendMessageModelAny(comunication);        

        return (ArrayList<Sell>) comunication.content.get(0);
    }
    
    @SuppressWarnings("unchecked")
    private HashMap<String, Product> getSellerItens(String seller){

        ArrayList<Object> content = new ArrayList<Object>();
        
        content.add(seller);
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.SELLER_EXIST, content);
        comunication = this.sendMessageModelAny(comunication);
        
        //	Caso do vendedor não existir retorna o nulo
        if(!(boolean)comunication.content.get(0) == true) {
        	return null;
        }

        comunication = null; comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, 
                EnumServices.GET_SELLER_ITENS, content);
                
        comunication = this.sendMessageModelAny(comunication);        


        return (HashMap<String, Product>)comunication.content.get(0);
    }
    


    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Parte do sistema                                    //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @return Somatório dos fundos na persistência
     */
    @SuppressWarnings("unused")
    private double getTotalFunds() {
        ArrayList<Object> content = null;
        
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.TOTAL_FUNDS_INT, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (double) comunication.content.get(0);
    }

    private boolean isFundsRight() {
        ArrayList<Object> content = null;
        
        Comunication comunication = new Comunication(EnumChannel.CONTROL_TO_MODEL, EnumServices.TOTAL_FUNDS_BOOL, content);
        comunication = this.sendMessageModelAny(comunication);
        
        return (boolean) comunication.content.get(0);
    }
    
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    //                                 Comunicacao                                         //
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    public void receive(Message msg) { //exibe mensagens recebidas
	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem informativa
		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
		Message poderia ser um registro contendo vários campos, para facilitar
	*/

		// DEBUG: neste exemplo, iremos apenas imprimir a mensagem que chegou
        System.out.println("" + msg.getSrc() + ": " + msg.getObject()+"(1)");
    }
    

    // responde requisições recebidas
    @Override
    public Object handle(Message message) { //} throws Exception{

	/*  No trabalho, vocês deverão verificar qual o tipo de mensagem requisitativa
		chegou e tratá-la conforme o caso. DICA: o objeto colocado dentro da
		Message poderia ser um registro contendo vários campos, para facilitar
	*/
    	
    	Comunication msg = (Comunication) message.getObject();
    	
    	System.out.println("Chegou a mensagem pro controle:\n" + (Comunication)msg);
    	
    	Comunication response = new Comunication();
    	
    	ArrayList<Object> content = new ArrayList<Object>();
    
    	if(msg.channel == EnumChannel.VIEW_TO_CONTROL) {
    		
    		if (msg.service == EnumServices.ADD_ITEM) {
    			// int add_product(String idSeller, String product, Float price, long amount, String description)
    			int var = this.add_product((String)msg.content.get(0), (String)msg.content.get(1), (float)msg.content.get(2),
    					(long)msg.content.get(3), (String)msg.content.get(4));
    			content.add(var);
    		}
    		
    		else if (msg.service == EnumServices.BUY_ITEM) {
    			// int purchase(String customer, String seller, String product, int amount)
    			int var = this.purchase((String)msg.content.get(0), (String)msg.content.get(1),
    					(String)msg.content.get(2), (int)msg.content.get(3));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.CREATE_CUSTOMER) {
    			//boolean add_customer(String id, String fullname, String password)
    			boolean var = this.add_customer((String)msg.content.get(0),(String)msg.content.get(1),
    					(String)msg.content.get(2));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.CREATE_SELLER) {
    			//boolean add_seller(String id, String fullname, String password)
    			boolean var = this.add_seller((String)msg.content.get(0),(String)msg.content.get(1),
    					(String)msg.content.get(2));
    			content.add(var);   			
    		}
    		
    		else if(msg.service == EnumServices.LIST_ITENS) {
    			//ArrayList<Product> list_products()
    			ArrayList<Product> var = this.list_products();
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.LOGIN_CUSTOMER) {
    			//boolean login_customer(String customer, String password)
    			boolean var = this.login_customer((String)msg.content.get(0),(String)msg.content.get(1));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.LOGIN_SELLER) {
    			//boolean login_seller(String seller, String password)
    			boolean var = this.login_seller((String)msg.content.get(0),(String)msg.content.get(1));
    			content.add(var);   			
    		}
    		
    		else if(msg.service == EnumServices.MAKE_QUESTION) {
    			//boolean sendQuestion(String customer, String product, String msg)
    			boolean var = this.sendQuestion((String)msg.content.get(0), (String)msg.content.get(1),
    					(String)msg.content.get(2));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.SEARCH_ITEM) {
    			//ArrayList<Product> search_product(String string)
    			ArrayList<Product> var = this.search_product((String)msg.content.get(0));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.SEND_ANSWER) {
    			//boolean sendAnswer(String seller, String product, String question, String answer)
    			boolean var = this.sendAnswer((String)msg.content.get(0), (String)msg.content.get(1),
    					(String)msg.content.get(2), (String)msg.content.get(3));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.BOUGHT_ITEMS) {
    			//ArrayList<Sell> getBougthItens(String customer)
    			ArrayList<Sell> var = this.getBougthItens((String)msg.content.get(0));
    			content.add(var);
    		}

    		// Resposta para quando um membro da visão manda um multicast avisando que é novo
    		//dai todos os membros do controle respondem para que ele adicione todos no seu vetor de endereços
    		else if(msg.service == EnumServices.NEW_VIEW_MEMBER) {
    			response.service = EnumServices.NEW_VIEW_MEMBER;
    			content = null;
    		}
    		
    		else if(msg.service == EnumServices.SOLD_ITEMS) {
    			//ArrayList<Sell> getBougthItens(String customer)
    			ArrayList<Sell> var = this.getSoldItens((String)msg.content.get(0));
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.TOTAL_FUNDS_BOOL) {
    			//boolean is_founds_right()
    			boolean var = this.isFundsRight();
    			content.add(var);
            }
            
            else if(msg.service == EnumServices.FOR_SALE_ITEMS){
                //HashMap<String, Product> getSellerItens(String seller)
                HashMap<String, Product> var = this.getSellerItens((String)msg.content.get(0));
                content.add(var);
            }
            
            response.service = msg.service;
    		response.channel = EnumChannel.CONTROL_TO_VIEW;
    		response.content = content;
    		
    	}
    	
    	else if(msg.channel == EnumChannel.CONTROL_TO_VIEW) {
    		
    		// Mensagem de quando um membro do controle manda para o grupo da visao falando que ele existe
    		//a mensagem vai acabar chegando para membros do controle que meio que ignoram a mesma
    		if(msg.service == EnumServices.NEW_CONTROL_MEMBER) {
    			response.service = EnumServices.NEW_CONTROL_MEMBER;
    			content = null;
    		}
    		
    		response.channel = EnumChannel.CONTROL_TO_CONTROL;
    		response.content = content;
    	}
    	
    	else if(msg.channel == EnumChannel.MODEL_TO_CONTROL) {
    		
    		// Multicast que o modelo vai dar e todos os controles precisam adicionar o endereço do modelo em questao
    		if(msg.service == EnumServices.NEW_MODEL_MEMBER) {
    			this.enderecosModelo.add(message.getSrc());
    			response.service = EnumServices.NEW_MODEL_MEMBER;
    			content = null;
    		}
    		
    		response.channel = EnumChannel.CONTROL_TO_MODEL;
    		response.content = content;
    	}
    
        
        System.out.println("Resposta do modelo \n "+response + "\n\n");

    	return response;
    }
    
    private Comunication sendMessageModelAny(Comunication comunication) {
        //System.out.println("Mandando mensagem pro modelo getFirst\n " + comunication);
    	Vector<Address> cluster = this.enderecosModelo;
    	//Address cluster = null;
        RequestOptions optinsResponse = new RequestOptions();
        optinsResponse.setMode(ResponseMode.GET_FIRST);
        // optionsResponse.setAnycasting(true);
        optinsResponse.setAnycasting(true);
        Message newMessage = new Message(null, comunication);
        RspList<Comunication> responseComunication = null;
        try {
            responseComunication = control_modelDispatcher.castMessage(cluster, newMessage, optinsResponse);
            System.out.println(responseComunication);
        } catch (Exception e) {
            e.printStackTrace();
        }
       
        return responseComunication.getFirst();
    }
    
    private RspList<Comunication> sendMessageModelAll(Comunication comunication) {
        //System.out.println("Mandando mensagem pro modelo getAll\n " + comunication);

    	Vector<Address> cluster = this.enderecosModelo;
    	//Address cluster = null;
        RequestOptions optinsResponse = new RequestOptions();
        optinsResponse.setMode(ResponseMode.GET_ALL);
        // optionsResponse.setAnycasting(true);
        optinsResponse.setAnycasting(true);
        //Message newMessage = new Message(cluster, comunication);
        Message newMessage = new Message(null, comunication);
        RspList<Comunication> responseComunication = null;
        try {
            responseComunication = control_modelDispatcher.castMessage(cluster, newMessage, optinsResponse);
            System.out.println(responseComunication);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseComunication;
    }

}