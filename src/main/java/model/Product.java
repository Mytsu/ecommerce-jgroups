package main.java.model;

import java.io.Serializable;
import java.util.ArrayList;

class Product implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    Product(String name, String description){
        this.id = 0;
        this.name = name;
        this.description = description;
        this.questions = new ArrayList<Question>();
        this.offersList = new ArrayList<Offer>();
        this.quantTotal = 0;
        this.idCurrentQuestion = 0;
        this.idCurrentOffer = 0;
    }

    public void add_offer(Offer offer){
        offer.id = this.idCurrentOffer;
        // offer.id = this.idCurrentOffer++; // OLHAR SE ESSA COISA FUNCIONA QUANDO TUDO PRONTO
        this.idCurrentOffer++;
        this.offersList.add(offer);
        this.quantTotal += offer.quant;
    }

    public void add_question(Question question){
        question.id = this.idCurrentQuestion;
        this.idCurrentQuestion++;
        this.questions.add(question);
    }
}