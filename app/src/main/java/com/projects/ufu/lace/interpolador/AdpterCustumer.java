package com.projects.ufu.lace.interpolador;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdpterCustumer<T> extends ArrayAdapter {
    List<T> objetos;


    public AdpterCustumer(Context context, int resource) {
         this(context, resource, 0, new ArrayList<>());
    }

    public AdpterCustumer(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

    }

    public AdpterCustumer(Context context, int resource, Object[] objects) {
        this(context, resource, 0, Arrays.asList(objects));

    }

    public AdpterCustumer(Context context, int resource, int textViewResourceId, Object[] objects) {
        this(context, resource, textViewResourceId, Arrays.asList(objects));

    }

    public AdpterCustumer(Context context, int resource, List objects) {
        super(context, resource, objects);
        objetos = objects;
    }

    public AdpterCustumer(Context context, int resource, int textViewResourceId, List objects) {
        super(context, resource, textViewResourceId, objects);
        objetos = objects;

    }

    public void setItem(T element, int position) {
        objetos.remove(position);
        objetos.add(position, element);
        notifyDataSetChanged();
    }
    public  void removeItem(int position){
        objetos.remove(position);
//         0 1
        if (position % 2 != 0) {
            position--;
        }
        objetos.remove(position);
        notifyDataSetChanged();
    }
}
