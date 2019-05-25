package model;

import java.io.Serializable;
import java.util.HashMap;

class Product implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    final String id;
    private String name, description;
    private HashMap<String, Question> questions;
    private HashMap<String, Offer> offers;
    private long count;

    Product(String id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
        this.questions = new HashMap<String, Question>();
        this.offers = new HashMap<String, Offer>();
        this.count = 0;
    }

    public void add_offer(Offer offer) {
        this.offers.put(offer.id, offer);
    }

    public void add_question(Question question) {
        this.questions.put(question.id, question);
    }
}