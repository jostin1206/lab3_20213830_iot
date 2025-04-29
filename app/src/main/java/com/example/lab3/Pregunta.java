package com.example.lab3;

public class Pregunta {
    private String question;
    private String correct_answer;
    private String incorrect_answers[];

    // Getters
    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correct_answer;
    }

    public String[] getIncorrectAnswers() {
        return incorrect_answers;
    }

}
