package main.java.model;

import java.io.Serializable;

class Answer implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    Answer(String sellerName, String answer){
        this.sellerName = sellerName;
        this.answer = answer;
    }
}