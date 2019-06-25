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
        // Caso já exista uma resposta do vendedor passado para esta pergunta
    	if(this.answers.containsKey(answer.sellerId)) {
            // Substitui-se a resposta antiga pela nova resposta passada
    		this.answers.get(answer.sellerId).answer = answer.answer;
        }
        // Caso contrario somente se coloca no hash essa nova pergunta
        this.answers.put(answer.sellerId, answer);
    }
}