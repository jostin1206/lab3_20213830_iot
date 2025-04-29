package com.example.lab3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EstadisticasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        // Recibimos los  datos del intent
        int correctas = getIntent().getIntExtra("correctas", 0);
        int incorrectas = getIntent().getIntExtra("incorrectas", 0);
        int sinResponder = getIntent().getIntExtra("sin_responder", 0);

        // Mostramos en textviews
        TextView txtCorrectas = findViewById(R.id.textViewCorrectas);
        TextView txtIncorrectas = findViewById(R.id.textViewIncorrectas);
        TextView txtSinResponder = findViewById(R.id.textViewSinResponder);

        txtCorrectas.setText(String.valueOf(correctas));
        txtIncorrectas.setText(String.valueOf(incorrectas));
        //txtSinResponder.setText(String.valueOf(sinResponder));

        if (sinResponder == 0) {
            txtSinResponder.setVisibility(View.GONE);
        } else {
            txtSinResponder.setText(String.valueOf(sinResponder));
        }


        // Botón para volver al menú
        Button btnVolver = findViewById(R.id.btnVolverMenu);
        btnVolver.setOnClickListener(v -> {
            Intent intent = new Intent(EstadisticasActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}