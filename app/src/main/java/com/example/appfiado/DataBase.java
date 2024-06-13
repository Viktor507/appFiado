package com.example.appfiado;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    String sqlCrear1 = "create table Deudas(id text primary key, nombre text, unidades int, fecha date, deuda double, imagen text)";
    String sqlCrear2 = "create table Inventario(id text primary key, cantidad int)";
    String sqlCrear3 = "create table Ventas(mes text primary key, unidades int, ganancias double)";
    String sqlEliminar;

    private Cursor cursor;

    String sqlSelect;

    String sqlSelect2;
    String sqlFilasMax = "SELECT MAX(id) FROM Deudas";
    String sqlFilasMax2 = "SELECT MAX(id) FROM Inventario";
    String sqlFilasNum = "SELECT COUNT(*) FROM Deudas";
    String quote1 = "SELECT unidades FROM Deudas where id";
    String quote2 = "SELECT cantidad FROM Inventario where id = 1";

    String quote3 = "SELECT unidades, ganancias FROM Ventas where mes";

    String sqlColumna;

    public DataBase(Context con, String DbDeudas, SQLiteDatabase.CursorFactory factory, int
            version) {
        super(con, DbDeudas, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCrear1);
        db.execSQL(sqlCrear2);
        db.execSQL(sqlCrear3);
    }


    //métodos


    public Cursor verData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from Deudas";
        cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor verData2() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from Ventas";
        cursor = db.rawQuery(query, null);

        return cursor;
    }

    public void eliminar(String id) {
        sqlEliminar = "delete from Deudas where id =" + id + "";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlEliminar);
    }

    public Cursor select(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        sqlSelect = quote1 + "=" + id + "";
        cursor = db.rawQuery(sqlSelect, null);
        return cursor;
    }

    public Cursor select2() {
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery(quote2, null);
        return cursor;
    }

    public Cursor select3(String f) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sqlSelect2 = quote3 + " = '" + f + "'";
        Cursor cursor = db.rawQuery(sqlSelect2, null);
        return cursor;
    }

    //    public void eliminarTodo(){
//        String sqlTodo = "delete from Tareas";
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(sqlTodo);
//    }
    public int numFila() {

        int numFila = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlFilasNum, null);

        if (cursor.moveToFirst()) {
            numFila = cursor.getInt(0);
        }

        cursor.close();

        return numFila;
    }


    public int maxFila() {

        int numFila = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlFilasMax, null);

        if (cursor.moveToFirst()) {
            numFila = cursor.getInt(0);
        }

        cursor.close();

        return numFila;
    }

    public int maxFila2() {

        int numFila = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlFilasMax2, null);

        if (cursor.moveToFirst()) {
            numFila = cursor.getInt(0);
        }

        cursor.close();

        return numFila;
    }

    public double sumColumna(Boolean b) {
        double suma = 0;
        if (b == false) {
            sqlColumna = "Select SUM(deuda) From Deudas";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(sqlColumna, null);

            if (cursor.moveToFirst()) {
                suma = cursor.getDouble(0);
            }
            cursor.close();
        }else{
            sqlColumna = "Select SUM(unidades) From Deudas";
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(sqlColumna, null);

            if (cursor.moveToFirst()) {
                suma = cursor.getDouble(0);
            }
            cursor.close();
        }
        return suma;
    }


    public Boolean insertar(String i, String n, int u, String f, double d) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cV = new ContentValues();
        cV.put("id", i);
        cV.put("nombre", n);
        cV.put("unidades", u);
        cV.put("fecha", f);
        cV.put("deuda", d);
        cV.put("imagen", "");
        db.insert("Deudas", null, cV);
        return true;
    }

    public Boolean insertar2(String i, int c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cV = new ContentValues();
        cV.put("id", i);
        cV.put("cantidad", c);
        db.insert("Inventario", null, cV);
        return true;
    }

    public Boolean insertar3(String m, int u,double g) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cV = new ContentValues();
        cV.put("mes", m);
        cV.put("unidades", u);
        cV.put("ganancias", g);
        db.insert("Ventas", null, cV);
        return true;
    }

    public Boolean update(String i, int u, double d, String f) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cV = new ContentValues();
        cV.put("unidades", u);
        cV.put("deuda", d);
        cV.put("fecha", f);
        db.update("Deudas", cV, "id = " + i + "", null);
        return true;
    }

    public Boolean updateimg(String i, String img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cV = new ContentValues();
        cV.put("imagen", img);
        db.update("Deudas", cV, "id = " + i + "", null);
        return true;
    }

    public Boolean updateinv(int c) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cV = new ContentValues();
        cV.put("cantidad", c);
        db.update("Inventario", cV, "id = " + 1 + "", null);
        return true;
    }

    public Boolean updateventas(String m, int u, double g) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cV = new ContentValues();
        cV.put("unidades", u);
        cV.put("ganancias", g);
        db.update("Ventas", cV, "mes = '" + m + "'", null);
        return true;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS Deudas");
        db.execSQL("DROP TABLE IF EXISTS Ventas");
        db.execSQL("DROP TABLE IF EXISTS Inventario");
    }
}
