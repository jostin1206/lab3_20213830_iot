package com.example.lab3;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface ApiService {
    @GET("api.php")
    Call<Respuesta> obtenerPreguntas(
            @Query("amount") int cantidad,
            @Query("category") int categoria,
            @Query("difficulty") String dificultad,
            @Query("type") String tipo
    );
}
