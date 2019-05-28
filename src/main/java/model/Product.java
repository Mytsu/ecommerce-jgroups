package model;

import java.io.Serializable;
import java.util.HashMap;

public class Product implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    final String id;
    private String name, description;
    private HashMap<String, Question> questions;
    private HashMap<String, Offer> offers;
    private long count;

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
        this.offers.put(offer.id, offer);
        this.count += offer.get_amount();
    }

    public void add_question(Question question) {
        // Adicionar uma nova pergunta
        this.questions.put(question.id, question);
        // Falta criar o aumento de pergunta caso ja exista
    }

    public double get_price(String seller) {
        return this.offers.get(seller).price;
    }

    public void deduce_amount(String seller, int amount) {
        this.offers.get(seller).amount -= amount;
        //FAZER RETIRADA DO VENDEDOR QUANDO O ESTOQUE DELE CHEGAR A 0
    }

    public boolean has_enougth(String seller, int amount) {
        if(this.offers.get(seller).get_amount() >= amount) {
            return true;
        }
        return false;
    }

}