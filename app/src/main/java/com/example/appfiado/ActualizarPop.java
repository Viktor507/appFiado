package com.example.appfiado;

import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActualizarPop extends AppCompatActivity {

    private EditText et1;
    private EditText et2;

    private Cursor cursor;

    private Cursor cursor2;

    private Button btn1;

    private Button btn2;

    private final double costoSoda = 0.60;
    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_actualizar);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        db = new DataBase(this, "DbDeudas", null, 1);

        DisplayMetrics medidas = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidas);

        int ancho = medidas.widthPixels;
        int alto = medidas.heightPixels;

        getWindow().setLayout((int) (ancho * 0.90), (int) (alto * 0.5));

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertarInventario();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int venta;
                double ganancia;
                if (!et2.getText().toString().equals("0") && !et2.getText().toString().equals("")) {
                    venta = Integer.parseInt(et2.getText().toString());
                    ganancia = venta * 0.60;
                    if (venta <= inventario()) {
                        ArrayList<Object> result = actualizarVentas(mesActual());
                        if (result.size() >= 2) {
                            venta = venta + (int) result.get(0);
                            ganancia = ganancia + (double) result.get(1);
                        }
                        db.updateventas(mesActual(), venta, ganancia);
                        db.updateinv(inventario() - venta);
                        Toast.makeText(getApplicationContext(), "Se ha registrado una venta de: " + venta + " sodas. Por: " + ganancia + " dólares", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Inventario insuficiente para realizar esta venta. Inventario restante: " + inventario() + " sodas.", Toast.LENGTH_LONG).show();
                        et2.setText("");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No se han ingresado datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ArrayList<Object> actualizarVentas(String f) {
        ArrayList<Object> al = new ArrayList();
        int v1 = 0;
        double v2 = 0;
        cursor2 = db.select3(f);

        if (cursor2 != null && cursor2.moveToFirst()) {
            do {
                v1 = cursor2.getInt(cursor2.getColumnIndexOrThrow("unidades"));
                v2 = cursor2.getDouble(cursor2.getColumnIndexOrThrow("ganancias"));

                al.add(v1);
                al.add(v2);

            } while (cursor2.moveToNext());
        }

        cursor2.close();

        return al;
    }

    public void insertarInventario() {
        int cantidad;
        if (!et1.getText().toString().equals("0") && !et1.getText().toString().equals("")) {
            if (db.maxFila2() == 0) {
                cantidad = Integer.parseInt(et1.getText().toString());
                db.insertar2("1", cantidad);
                Toast.makeText(getApplicationContext(), "Inventario registrado", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                cantidad = Integer.parseInt(et1.getText().toString());
                db.updateinv(cantidad);
                Toast.makeText(getApplicationContext(), "Inventario actualizado", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No se han ingresado datos", Toast.LENGTH_SHORT).show();
        }
    }

    private String mesActual() {
        String mesActual = "";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        mesActual = sdf.format(calendar.getTime());

        return mesActual;
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