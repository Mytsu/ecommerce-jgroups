package system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Product implements Serializable {

    @Override
	public String toString() {
		return "Product [id=" + id + ", description=" + description + ", count=" + count + "]";
	}

	private static final long serialVersionUID = 4506509784967298618L;
    public String id, description;
    public HashMap<String, Question> questions;
    public HashMap<String, Offer> offers;
    public long count;

    public Product(String id, String description) {
        this.id = id;
        this.description = description;
        this.questions = new HashMap<String, Question>();
        this.offers = new HashMap<String, Offer>();
        this.count = 0;
    }

    public void add_offer(Offer offer) {
        // CRIAR SE JA EXISTE OFERTA
        // ATUALIZAR PRECO E ADICIONAR A QUANTIDADE
    	if(!offers.containsKey(offer.id)) {
    		this.offers.put(offer.id, offer);
            this.count += offer.get_amount();
            return ;
    	}
<<<<<<< HEAD
        
        this.count += offer.amount;
        this.offers.get(offer.id).price = offer.price;
=======
    	
        this.offers.get(offer.id).price = offer.price;
        this.count += offer.amount;
>>>>>>> refs/remotes/origin/master
    	this.offers.get(offer.id).amount += offer.amount;
    	
    	return ;        
    }

    public boolean add_question(Question question) {

    	// Adicionar uma nova pergunta
    	if(! questions.containsKey(question.id)) {
    		this.questions.put(question.id, question);
    		return true;
    	}
    	// Caso a pergunta ja exista retorna-se falso
    	return false;
        
        // Falta criar o aumento de pergunta caso ja exista
    }

    public double get_price(String seller) {
        return this.offers.get(seller).price;
    }

    public Map<String, Offer> getOffers() {
        return this.offers;
    }

    public Map<String, Question> getQuestions() {
        return this.questions;
    }

    public void deduce_amount(String seller, int amount) {
        this.offers.get(seller).amount -= amount;
        // TODO FAZER RETIRADA DO VENDEDOR QUANDO O ESTOQUE DELE CHEGAR A 0
    }

    public boolean has_enougth(String seller, int amount) {
        if(this.offers.get(seller).get_amount() >= amount) {
            return true;
        }
        return false;
    }

}