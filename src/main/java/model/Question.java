package model;

import java.io.Serializable;
import java.util.HashMap;;

class Question implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    final String id;
    private String userId;
    private String question;
    private HashMap<String, Answer> answers;

    Question(String userId, String question){
        this.userId = userId;
        this.question = question;
        this.answers = new HashMap<String, Answer>();
        this.id = "";
    }

    public void add_answer(Answer answer){
        this.answers.put(answer.id, answer);
    }
}