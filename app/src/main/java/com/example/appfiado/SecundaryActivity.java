package com.example.appfiado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SecundaryActivity extends AppCompatActivity {

    private Button btnDeudas;

    private ImageButton btnActualizar;

    private Adapter2 adapter2;
    private ListView lstv2;
    private List<Datos2> lst2 = new ArrayList<>();
    private DataBase db;
    private Cursor cursor;
    private DecimalFormat decimalFormat1 = new DecimalFormat("###,###.00");
    private DecimalFormat decimalFormat2 = new DecimalFormat("#");
    private TextView txtv1;
    private TextView txtv2;
    private TextView txtv3;

    private TextView txtv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secundary);
        btnDeudas = findViewById(R.id.btndeudas);

        btnActualizar = findViewById(R.id.imgbtn);
        txtv1 = findViewById(R.id.valor1);
        txtv2 = findViewById(R.id.valor2);
        txtv3 = findViewById(R.id.valor3);
        txtv4 = findViewById(R.id.valor4);

        db = new DataBase(this, "DbDeudas", null, 1);

        db.insertar3(mesActual(), 0, 0);

        adapter2 = new Adapter2(SecundaryActivity.this, lst2, this);
        lstv2 = findViewById(R.id.lstv2);
        lstv2.setTextFilterEnabled(true);
        cargarInforme();

        btnDeudas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecundaryActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SecundaryActivity.this, ActualizarPop.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarInforme();
    }

    public void cargarInforme() {
        decimalFormat1.setMinimumIntegerDigits(1);
        txtv1.setText(decimalFormat1.format(db.sumColumna(false))+" $");
        txtv2.setText(String.valueOf(db.numFila()));
        txtv3.setText(decimalFormat2.format(db.sumColumna(true))+" uds");
        txtv4.setText("Inventario disponible: " + actualizarInventario() + " uds");

        //bases de datos
        lst2.clear();
        Cargarventas();
    }

    public int actualizarInventario() {
        cursor = db.select2();
        int v1 = 0;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                v1 = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));
            } while (cursor.moveToNext());
        }

        return v1;
    }

    private String mesActual() {
        String mesActual = "";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        mesActual = sdf.format(calendar.getTime());

        return mesActual;
    }

    private void Cargarventas() {
        String v1 = "";
        int v2 = 0;
        double v3 = 0;


        cursor = db.verData2();

        if (cursor != null && cursor.moveToFirst()) {
            do {

                v1 = cursor.getString(cursor.getColumnIndexOrThrow("mes"));
                v2 = cursor.getInt(cursor.getColumnIndexOrThrow("unidades"));
                v3 = cursor.getDouble(cursor.getColumnIndexOrThrow("ganancias"));
                lst2.add(new Datos2(v1, v2, v3));

            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter2 = new Adapter2(SecundaryActivity.this, lst2, this);
        lstv2.setAdapter(adapter2);
        Collections.sort(lst2);

    }
}