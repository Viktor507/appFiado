package com.example.appfiado;

public class Datos implements Comparable<Datos> {
    public String id;
    public String n;
    public double d;
    public int u;
    public String f;

    public String img;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public int getU() {
        return u;
    }

    public void setU(int u) {
        this.u = u;
    }

    public String getF() {
        return f;
    }

    public void setF(String f) {
        this.f = f;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Datos(String id, String nombre, int unidades, String fecha, double deuda, String imagen) {
        this.id = id;
        this.n = nombre;
        this.u = unidades;
        this.f = fecha;
        this.d = deuda;
        this.img = imagen;

    }



    @Override
    public int compareTo(Datos datos) {
        return id.compareTo(datos.getId());
    }
}
