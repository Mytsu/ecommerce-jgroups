package model;

import java.io.Serializable;

public class Answer implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    final String id;
    private String sellerId, answer;

    Answer(String id, String sellerId, String answer) {
        this.id = id;
        this.sellerId = sellerId;
        this.answer = answer;
    }
}