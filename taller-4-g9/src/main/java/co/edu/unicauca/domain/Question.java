package co.edu.unicauca.domain;

public class Question {

    private int id;
    private String name;
    private String question;
    private QuestionDistractors distractors;
    private String correctAnswer;
    private String status;

    public Question(
        int id,
        String name,
        String question,
        QuestionDistractors distractors,
        String correctAnswer,
        String status) {

        this.id = id;
        this.name = name;
        this.question = question;
        this.distractors = distractors;
        this.correctAnswer = correctAnswer;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getQuestion() {
        return question;
    }

    public QuestionDistractors getDistractors() {
        return distractors;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

