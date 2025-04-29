package com.example.lab3;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.lab3.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private boolean conexionOk = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Llenano Spinner de categoría
        String[] categorias = {
                "Cultura General", "Libros", "Películas", "Música",
                "Computación", "Matemática", "Deportes", "Historia"
        };
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(adapterCategoria);

        // Llenado de Spinner de Dificultad
        String[] dificultades = {"fácil", "medio", "difícil"};
        ArrayAdapter<String> adapterDificultad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dificultades);
        adapterDificultad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDificultad.setAdapter(adapterDificultad);

        // Btón para comprobar conexión
        binding.btnComprobarConexion.setOnClickListener(v -> {
            if (hayConexion()) {
                conexionOk = true;
                Toast.makeText(this, "Conexión a internet exitosa", Toast.LENGTH_SHORT).show();
                binding.btnComenzar.setEnabled(true);
            } else {
                conexionOk = false;
                Toast.makeText(this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
                binding.btnComenzar.setEnabled(false);
            }
        });

        //  Botón Comenzar
        binding.btnComenzar.setOnClickListener(v -> {
            String cantidadStr = binding.cantidad.getText().toString();
            String categoria = binding.spinnerCategoria.getSelectedItem().toString();
            String dificultad = binding.spinnerDificultad.getSelectedItem().toString();

            // Validamos campos
            if (!conexionOk || cantidadStr.isEmpty() || categoria.isEmpty() || dificultad.isEmpty()) {
                Toast.makeText(this, "⚠️ Complete todos los campos y verifique la conexión", Toast.LENGTH_SHORT).show();
                return;
            }

            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                Toast.makeText(this, "⚠️ La cantidad debe ser un número positivo", Toast.LENGTH_SHORT).show();
                return;
            }

            // creamos el Intent para abrir TriviaActivity
            Intent intent = new Intent(MainActivity.this, TriviaActivity.class);
            intent.putExtra("categoria", categoria);
            intent.putExtra("cantidad", cantidad);
            intent.putExtra("dificultad", dificultad);
            startActivity(intent);
        });

    }

    // esta función será para validar la conexión a internet
    private boolean hayConexion() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }
}