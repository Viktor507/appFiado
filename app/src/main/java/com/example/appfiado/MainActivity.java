package com.example.appfiado;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{


    private ListView lstv;
    private Adapter adapter;
    private DataBase db;
    private List<Datos> lst = new ArrayList<>();
    private ImageButton btn1;
    private Button btnInfo;
    private SearchView srchv;

    private ImageView imgv;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnInfo = findViewById(R.id.btninfo);
        imgv = findViewById(R.id.imgvf);

        srchv = findViewById(R.id.srch);
        db = new DataBase(this, "DbDeudas", null, 1);
        adapter = new Adapter(MainActivity.this, lst, this);
        lstv = findViewById(R.id.lstv1);
        lstv.setTextFilterEnabled(true);

        btn1 = findViewById(R.id.imgbtn);
        CargarDeudas();
        placeholder();

        srchv.setOnQueryTextListener(this);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SecundaryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }
    }


    public void AgregarDeuda(View view) {
        startActivity(new Intent(MainActivity.this, RegistrarPop.class));

    }

    private void CargarDeudas() {
        String v1 = "";
        String v2 = "";
        int v3 = 0;
        String v4 = "";
        double v5 = 0;
        String v6 = "";

        cursor = db.verData();

        if (cursor != null && cursor.moveToFirst()) {
            do {

                v1 = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                v2 = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                v3 = cursor.getInt(cursor.getColumnIndexOrThrow("unidades"));
                v4 = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                v5 = cursor.getDouble(cursor.getColumnIndexOrThrow("deuda"));
                v6 = cursor.getString(cursor.getColumnIndexOrThrow("imagen"));
                lst.add(new Datos(v1, v2, v3, v4, v5, v6));

            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter = new Adapter(MainActivity.this, lst, this);
        lstv.setAdapter(adapter);
        Collections.sort(lst, Comparator.comparingDouble(Datos::getD).reversed());

    }

    @Override
    protected void onResume() {
        super.onResume();
        placeholder();
        actualizarTareas();
    }

    public void actualizarTareas() {
        lst.clear();
        CargarDeudas();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            adapter.onActivityResult(requestCode, resultCode, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (adapter != null) {
            if (TextUtils.isEmpty(newText)) {
                actualizarTareas();
            } else {
                adapter.getFilter().filter(newText);
            }
        }
        return true;
    }

    private void placeholder(){
        if (db.maxFila()>0){
            imgv.setVisibility(View.INVISIBLE);
        }else{
            imgv.setVisibility(View.VISIBLE);
        }
    }
}