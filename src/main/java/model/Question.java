package model;

import java.io.Serializable;
import java.util.HashMap;;

class Question implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    final String id;
    private String userId;
    private String question;
    private HashMap<String, Answer> answers;

    Question(String id, String userId, String question) {
        this.id = id;
        this.userId = userId;
        this.question = question;
        this.answers = new HashMap<String, Answer>();
    }

    public void add_answer(Answer answer){
        this.answers.put(answer.id, answer);
    }
}