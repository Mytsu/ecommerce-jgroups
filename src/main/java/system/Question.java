package system;

import java.io.Serializable;
import java.util.HashMap;

public class Question implements Serializable {

    private static final long serialVersionUID = 4506509784967298618L;
    public String id;
    public String userId;
    private HashMap<String, Answer> answers;

    public Question(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.answers = new HashMap<String, Answer>();
    }

    public void add_answer(Answer answer){
    	if(this.answers.containsKey(answer.sellerId)) {
    		this.answers.get(answer.sellerId).answer = answer.answer;
    	}
        this.answers.put(answer.sellerId, answer);
    }
}