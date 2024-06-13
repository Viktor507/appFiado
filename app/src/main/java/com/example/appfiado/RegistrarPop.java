package com.example.appfiado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistrarPop extends AppCompatActivity {

    private EditText et1;
    private EditText et2;

    private final double costoSoda = 0.60;

    private DataBase db;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_registrar);

        db = new DataBase(this, "DbDeudas", null, 1);

        DisplayMetrics medidas = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidas);

        int ancho = medidas.widthPixels;
        int alto = medidas.heightPixels;

        getWindow().setLayout((int) (ancho * 0.90), (int) (alto * 0.5));

        et1 = findViewById(R.id.etnombre);
        et2 = findViewById(R.id.etcantidad);

    }

    public void insertarDeuda(View view) {
        if (db.maxFila2() != 0) {
            if (!et1.getText().toString().equals("") && !et2.getText().toString().equals("")) {
                String nombre = et1.getText().toString();
                int unidades = Integer.parseInt(et2.getText().toString());
                String fecha = fechaHoy();
                String id = idNumber();
                double deuda = calcularDeuda(unidades);
                if (unidades <= inventario()) {
                    db.insertar(id, nombre, unidades, fecha, deuda);
                    db.updateinv(inventario()-unidades);
                    Toast.makeText(getApplicationContext(), "¡La deuda ha sido registrada!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "¡Inventario insuficiente para relizar esta operación! Inventario restante: "+inventario()+" refrescos", Toast.LENGTH_LONG).show();
                    et2.setText("");
                }
            } else {
                Toast.makeText(getApplicationContext(), "Faltan datos por ingresar", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent intent = new Intent(RegistrarPop.this, SecundaryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
            Toast.makeText(getApplicationContext(), "Asegurese de registrar el inventario primero", Toast.LENGTH_LONG).show();
        }

    }

    public String fechaHoy() {
        String fecha;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        fecha = dateFormat.format(calendar.getTime());
        return fecha;
    }

    public String idNumber() {
        String id = "";
        int sumafilas = db.maxFila() + 1;
        id = String.valueOf(sumafilas);
        return id;
    }

    public double calcularDeuda(int u) {
        double deuda = u * costoSoda;

        return deuda;
    }

    private int inventario() {
        cursor = db.select2();
        int v1 = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                v1 = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return v1;
    }
}