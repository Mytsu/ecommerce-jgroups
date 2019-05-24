package main.java.model;

import java.io.Serializable;
import java.util.ArrayList;

class Question implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;

    Question(String userName, String question){
        this.userName = userName;
        this.question = question;
        this.answers = new ArrayList<Answer>();
        this.id = 0;
    }

    public void add_answer(Answer answer){
        this.answers.add(answer);
    }
}