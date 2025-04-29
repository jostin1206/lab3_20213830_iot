package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab3.databinding.ActivityTriviaBinding;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TriviaActivity extends AppCompatActivity {
    private ActivityTriviaBinding binding;

    private String categoria;
    private int cantidad;
    private String dificultad;
    private CountDownTimer countDownTimer;
    private long tiempoTotalMs;
    private List<Pregunta> preguntas;
    private int indicePreguntaActual = 0;

    private int totalCorrectas = 0;
    private int totalIncorrectas = 0;

    private int totalSinResponder = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTriviaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recibimos los datos enviados desde MainActivity
        Intent intent = getIntent();
        if (intent != null) {
            categoria = intent.getStringExtra("categoria");
            cantidad = intent.getIntExtra("cantidad", 0);
            dificultad = intent.getStringExtra("dificultad");
        }

        // aqiu traducimos dificultad a inglés para la API
        if (dificultad != null) {
            switch (dificultad.toLowerCase()) {
                case "fácil":
                case "facil":
                    dificultad = "easy";
                    break;
                case "medio":
                    dificultad = "medium";
                    break;
                case "difícil":
                case "dificil":
                    dificultad = "hard";
                    break;
            }
        }

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        int categoriaId = obtenerCategoriaId(categoria);
        String tipo = "boolean";

        // Llamamos a la API
        Call<Respuesta> call = apiService.obtenerPreguntas(cantidad, categoriaId, dificultad.toLowerCase(), tipo);

        call.enqueue(new Callback<Respuesta>() {
            @Override
            public void onResponse(Call<Respuesta> call, Response<Respuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    preguntas = response.body().getResults();

                    // Debug para ver cuántas preguntas llegaron
                    //Toast.makeText(TriviaActivity.this, "Preguntas recibidas: " + preguntas.size(), Toast.LENGTH_SHORT).show();

                    if (!preguntas.isEmpty()) {
                        mostrarPregunta();

                        long segundosPorPregunta;
                        switch (dificultad.toLowerCase()) {
                            case "easy":
                                segundosPorPregunta = 5;
                                break;
                            case "medium":
                                segundosPorPregunta = 7;
                                break;
                            case "hard":
                                segundosPorPregunta = 10;
                                break;
                            default:
                                segundosPorPregunta = 5;
                                break;
                        }

                        tiempoTotalMs = cantidad * segundosPorPregunta * 1000;
                        iniciarContador();

                    } else {
                        //Toast.makeText(TriviaActivity.this, "No llegaron preguntas", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TriviaActivity.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Respuesta> call, Throwable t) {
                Toast.makeText(TriviaActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //boton de qye se  habilita al seleccionar respuesta
        binding.radioGroupOpciones.setOnCheckedChangeListener((group, checkedId) -> {
            binding.btnSiguiente.setEnabled(true);
        });

        // Botón "Siguiente"
        binding.btnSiguiente.setOnClickListener(v -> {
            if (indicePreguntaActual < preguntas.size() - 1) {
                int selectedId = binding.radioGroupOpciones.getCheckedRadioButtonId();
                if (selectedId != -1) { // Si eligió una respuesta
                    RadioButton selectedRadio = findViewById(selectedId);
                    String respuestaUsuario = selectedRadio.getText().toString().toLowerCase();
                    String respuestaCorrecta = preguntas.get(indicePreguntaActual).getCorrectAnswer().toLowerCase();

                    if (respuestaUsuario.equals(respuestaCorrecta)) {
                        totalCorrectas++;
                    } else {
                        totalIncorrectas++;
                    }
                }
                indicePreguntaActual++;
                mostrarPregunta();
            } else {
                //int respondidas = totalCorrectas + totalIncorrectas;
                //int sinResponder = cantidad - respondidas;
                int sinResponder = 0;  // Si llegó al final con todas respondidas, no hay preguntas sin responder


                Intent intentResultado = new Intent(TriviaActivity.this, EstadisticasActivity.class);
                intentResultado.putExtra("correctas", totalCorrectas);
                intentResultado.putExtra("incorrectas", totalIncorrectas);
                intentResultado.putExtra("sin_responder", sinResponder);
                startActivity(intentResultado);
                finish();


            }
        });
    }

    private void iniciarContador() {
        countDownTimer = new CountDownTimer(tiempoTotalMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long segundosRestantes = millisUntilFinished / 1000;
                long minutos = segundosRestantes / 60;
                long segundos = segundosRestantes % 60;
                binding.textViewTiempo.setText(String.format("⏱ %02d:%02d", minutos, segundos));
            }

            @Override
            public void onFinish() {
                Toast.makeText(TriviaActivity.this, "¡Tiempo terminado!", Toast.LENGTH_SHORT).show();

                int respondidas = totalCorrectas + totalIncorrectas;
                int sinResponder = cantidad - respondidas;  // Esto es correcto

                Intent intent = new Intent(TriviaActivity.this, EstadisticasActivity.class);
                intent.putExtra("correctas", totalCorrectas);
                intent.putExtra("incorrectas", totalIncorrectas);

                // Solo enviamos sin_responder si hay preguntas no contestadas
                if (sinResponder > 0) {
                    intent.putExtra("sin_responder", sinResponder);
                }
                startActivity(intent);
                finish();
            }


        }.start();
    }

    private void mostrarPregunta() {
        Pregunta preguntaActual = preguntas.get(indicePreguntaActual);
        binding.textViewPregunta.setText(preguntaActual.getQuestion());
        binding.textViewNumeroPregunta.setText("Pregunta " + (indicePreguntaActual + 1) + "/" + preguntas.size());

        binding.radioGroupOpciones.clearCheck();
        binding.btnSiguiente.setEnabled(false);
    }

    private int obtenerCategoriaId(String categoria) {
        switch (categoria) {
            case "Cultura General": return 9;
            case "Libros": return 10;
            case "Películas": return 11;
            case "Música": return 12;
            case "Computación": return 18;
            case "Matemática": return 19;
            case "Deportes": return 21;
            case "Historia": return 23;
            default: return 9;
        }
    }
}
