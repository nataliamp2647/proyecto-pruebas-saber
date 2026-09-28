package co.edu.unicauca.microkernel.common;

public class QuestionRequest {

    private String name;
    private String question;
    private String type;

    public QuestionRequest(
            String name,
            String question,
            String type) {

        this.name = name;
        this.question = question;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public String getType() {
        return type;
    }
}

