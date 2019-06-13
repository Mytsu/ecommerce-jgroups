package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.ReceiverAdapter;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.util.Rsp;
import org.jgroups.util.RspList;

import system.*;

public class Persistence extends ReceiverAdapter implements RequestHandler, Serializable{

    private static final long serialVersionUID = 4506509784967298618L;
	
    private CustomerDAO customers;
    public SellerDAO sellers;
    public ProductDAO products;
    
    public JChannel control_modelChannel ;
    private MessageDispatcher control_modelDispatcher;
    
    Persistence() throws Exception{
    	
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
        
        this.montaGrupo();
        
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
        

    private HashMap<String, Product> getItens() {
    	return products.get_products();
    }
    
    private HashMap<String, Customer> getCustomers(){
    	return customers.get_customers();
    }
    
    private HashMap<String, Seller> getSellers() {
    	return sellers.get_sellers();
    }
    
    // responde requisições recebidas
    @Override
    public Object handle(Message message) throws Exception{
    	
    	Comunication msg = (Comunication)message.getObject();    	
        
    	Comunication response = new Comunication();
    		
    	ArrayList<Object> content = new ArrayList<Object>();
    	
    	if(msg.channel == EnumChannel.CONTROL_TO_MODEL) {
    		
    		if(msg.service == EnumServices.GET_ITENS) {
    			
    			response.service = EnumServices.GET_ITENS;
    			HashMap<String, Product> var = this.getItens();
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.GET_CUSTOMERS) {
    			
    			response.service = EnumServices.GET_CUSTOMERS;
    			HashMap<String, Customer> var = this.getCustomers();
    			content.add(var);
    		}
    		
    		else if(msg.service == EnumServices.GET_SELLERS) {
    			
    			response.service = EnumServices.GET_SELLERS;
    			HashMap<String, Seller> var = this.getSellers();
    			content.add(var);
    		}
    		
    		// Resposta para quando um membro da controle manda um multicast avisando que é novo
    		//dai todos os membros do modelo respondem para que ele adicione todos no seu vetor de endereços
    		else if(msg.service == EnumServices.NEW_CONTROL_MEMBER) {
    			response.service = EnumServices.NEW_CONTROL_MEMBER;
    			content = null;
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
        
        
        	return response;
        }

		
}
