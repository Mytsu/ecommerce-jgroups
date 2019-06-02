package system;

import java.io.Serializable;

public class Answer implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    public String sellerId, answer;

    public Answer(String sellerId, String answer) {
        this.sellerId = sellerId;
        this.answer = answer;
    }
}