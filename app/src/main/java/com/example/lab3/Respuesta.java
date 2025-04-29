package com.example.lab3;

import java.util.List;

public class Respuesta {
    private int response_code;
    private List<Pregunta> results;

    public int getResponseCode() {
        return response_code;
    }

    public List<Pregunta> getResults() {
        return results;
    }
}
