package com.example.appfiado;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class Adapter extends BaseAdapter implements Filterable {
    Context context;
    List<Datos> lst;

    private List<Datos> filteredList;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###.00");

    private OnActivityResultListener listener;
    private String unidades;
    private DataBase db;
    private Cursor cursor;

    private Cursor cursor2;
    private int newunidades;
    private double newdeuda;
    private String ImageId;
    private MainActivity actividad;
    private static final int REQUEST_SELECT_IMAGE = 100;
    private static final int REQUEST_CAPTURE_PHOTO = 200;
    RegistrarPop rp = new RegistrarPop();

    CustomFilter filter;

    private static final int REQUEST_PERMISSION_CODE = 123;


    public Adapter(Context context, List<Datos> lst, MainActivity actividad) {
        this.context = context;
        this.lst = lst;
        this.actividad = actividad;
        this.filteredList = lst;
    }

    public void setOnActivityResultListener(OnActivityResultListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return lst.size();
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
        TextView txt4;

        Datos d = lst.get(i);

        if (view == null)
            view = LayoutInflater.from(context).inflate(R.layout.lstv, null);

        ImageButton btnItem1 = view.findViewById(R.id.btnmas);
        ImageButton btnItem2 = view.findViewById(R.id.btnmenos);
        ImageView imgv = view.findViewById(R.id.img);

        imgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageId = d.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Seleccionar fuente de imagen");
                String[] options = {"Galería", "Cámara"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            ((Activity) context).startActivityForResult(intent, REQUEST_SELECT_IMAGE);
                        } else if (which == 1) {
                            dispatchTakePictureIntent();
                        }
                    }
                });
                builder.show();
            }
        });


        btnItem1.setOnClickListener(new View.OnClickListener() { //boton agregar
            @Override
            public void onClick(View v) {
                db = new DataBase(context, "DbDeudas", null, 1);
                if (inventario() >= 1) {
                    cursor = db.select(d.getId());
                    if (cursor.moveToFirst()) {
                        int unidadesIndex = cursor.getColumnIndex("unidades");
                        unidades = cursor.getString(unidadesIndex);
                        newunidades = d.getU() + 1;
                        newdeuda = d.getD() + 0.60;
                        db.update(d.getId(), newunidades, newdeuda, rp.fechaHoy());
                        db.updateinv(inventario() - 1);
                        Toast.makeText(context, "¡La deuda ha sido actualizada!", Toast.LENGTH_SHORT).show();
                        reiniciarActividadConAnimacion();
                    }
                } else {
                    Toast.makeText(context, "¡Inventario insuficiente para relizar esta operación! Inventario restante: " + inventario() + " refrescos", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnItem2.setOnClickListener(new View.OnClickListener() { //boton restar
            @Override
            public void onClick(View v) {
                int venta = 1;
                double ganancia = 0.60;
                db = new DataBase(context, "DbDeudas", null, 1);
                cursor = db.select(d.getId());
                if (cursor.moveToFirst()) {
                    int unidadesIndex = cursor.getColumnIndex("unidades");
                    if (cursor.getInt(unidadesIndex) == 1) {
                        ArrayList<Object> result = actualizarVentas(mesActual());
                        if (result.size() >= 2) {
                            venta = venta + (int) result.get(0);
                            ganancia = ganancia + (double) result.get(1);
                        }
                        db.eliminar(d.getId());
                        db.updateventas(mesActual(), venta, ganancia);
                        Toast.makeText(context, "¡La deuda ha sido totalmente pagada!", Toast.LENGTH_SHORT).show();
                        reiniciarActividadConAnimacion();
                    } else {
                        ArrayList<Object> result = actualizarVentas(mesActual());
                        if (result.size() >= 2) {
                            venta = venta + (int) result.get(0);
                            ganancia = ganancia + (double) result.get(1);
                        }
                        newunidades = d.getU() - 1;
                        newdeuda = d.getD() - 0.60;
                        db.update(d.getId(), newunidades, newdeuda, rp.fechaHoy());
                        db.updateventas(mesActual(), venta, ganancia);
                        Toast.makeText(context, "¡La deuda ha sido actualizada!", Toast.LENGTH_SHORT).show();
                        reiniciarActividadConAnimacion();
                    }

                }

            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
                dialogo1.setTitle("Pregunta del sistema");
                dialogo1.setMessage("¿Eliminar Deudor?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Datos objetoSeleccionado = lst.get(i);
                        String idDatos = objetoSeleccionado.getId();
                        DataBase db = new DataBase(context, "DbDeudas", null, 1);
                        db.eliminar(idDatos);
                        lst.remove(objetoSeleccionado);
                        notifyDataSetChanged();
                        Toast.makeText(context, "¡Deudor eliminado!", Toast.LENGTH_SHORT).show();
                        reiniciarActividadConAnimacion();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(context, "Operación cancelada", Toast.LENGTH_SHORT).show();
                    }

                });
                dialogo1.show();
                return true;
            }


        });

        Datos dato = null;
        if (!filteredList.isEmpty()) {
            dato = filteredList.get(i);
        }

        if (dato != null) {
            txt1 = view.findViewById(R.id.txtnombre);
            txt2 = view.findViewById(R.id.txtcantidad);
            txt3 = view.findViewById(R.id.txtdeuda);
            txt4 = view.findViewById(R.id.txtfecha);

            txt1.setText(dato.getN().toUpperCase());
            txt2.setText(dato.getU() + " sodas");
            decimalFormat.setMinimumIntegerDigits(1);
            txt3.setText(decimalFormat.format(dato.getD()));
            txt4.setText(dato.getF());

            if (!dato.getImg().equals("")) {
                Glide.with(context)
                        .load(new File(dato.getImg()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .centerInside()
                        .into(imgv);
            }
        }

        return view;
    }

    private ArrayList<Object> actualizarVentas(String f) {
        ArrayList <Object> al = new ArrayList();
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

    public void reiniciarActividadConAnimacion() {
        Intent intent = new Intent(actividad, MainActivity.class);
        actividad.startActivity(intent);
        actividad.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        actividad.finish();
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) throws IOException {
        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = actividad.getContentResolver().query(selectedImageUri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();
                    db = new DataBase(context, "DbDeudas", null, 1);
                    if (imagePath != null) {
                        db.updateimg(ImageId, imagePath);
                    }
                    reiniciarActividadConAnimacion();
                }
            }
        }


    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        String currentPhotoPath = image.getAbsolutePath();
        db = new DataBase(context, "DbDeudas", null, 1);
        db.updateimg(ImageId, currentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent() {
        try {
            File photoFile = createImageFile();
            Uri photoURI = FileProvider.getUriForFile(context, "com.example.appfiado.fileprovider", photoFile);

            String currentPhotoPath = photoFile.getAbsolutePath();
            db = new DataBase(context, "DbDeudas", null, 1);
            db.updateimg(ImageId, currentPhotoPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            ((Activity) context).startActivityForResult(intent, REQUEST_CAPTURE_PHOTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int inventario() {
        db = new DataBase(context, "DbDeudas", null, 1);
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

    private String mesActual() {
        String mesActual = "";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        mesActual = sdf.format(calendar.getTime());

        return mesActual;
    }


    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            List<Datos> filtros = new ArrayList<>();

            if (charSequence != null && charSequence.length() > 0) {
                String filterPattern = charSequence.toString().toUpperCase().trim();

                for (Datos dato : lst) {
                    if (dato.getN().toUpperCase().startsWith(filterPattern)) {
                        filtros.add(dato);
                    }
                }
            } else {
                filtros.addAll(lst);
            }

            filterResults.count = filtros.size();
            filterResults.values = filtros;
            return filterResults;
        }


        @Override
        protected void publishResults(CharSequence charSequence, FilterResults resultadosFiltro) {
            filteredList.clear();
            filteredList.addAll((List<Datos>) resultadosFiltro.values);
            notifyDataSetChanged();
        }
    }

}




