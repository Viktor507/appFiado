package com.example.appfiado;

public class Datos2 implements Comparable<Datos2> {

    public String mes;
    public int uds;
    public double gnc;

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public int getUds() {
        return uds;
    }

    public void setUds(int uds) {
        this.uds = uds;
    }

    public double getGnc() {
        return gnc;
    }

    public void setGnc(double gnc) {
        this.gnc = gnc;
    }

    public Datos2(String mes, int unidades, double ganancias) {
        this.mes = mes;
        this.uds = unidades;
        this.gnc = ganancias;

    }

    @Override
    public int compareTo(Datos2 datos2) {
        return mes.compareTo(datos2.getMes());
    }
}
