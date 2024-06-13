package com.example.appfiado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class Adapter2 extends BaseAdapter {

    private List<Datos2> lst2;
    private Context context;

    private DecimalFormat decimalFormat = new DecimalFormat("###,###.00");

    private SecundaryActivity actividad;

    public Adapter2(Context context, List<Datos2> lst, SecundaryActivity actividad) {
        this.context = context;
        this.lst2 = lst;
        this.actividad = actividad;
    }

    @Override
    public int getCount() {
        return lst2.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView txt1;
        TextView txt2;
        TextView txt3;

        Datos2 d2 = lst2.get(i);

        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.lstv2, null);

        txt1 = view.findViewById(R.id.txtmes);
        txt2 = view.findViewById(R.id.txtunidades);
        txt3 = view.findViewById(R.id.txtganancia);

        txt1.setText(d2.getMes());
        txt2.setText(d2.getUds() + " sodas");
        decimalFormat.setMinimumIntegerDigits(1);
        txt3.setText(decimalFormat.format(d2.getGnc()));
        return view;
    }
}
