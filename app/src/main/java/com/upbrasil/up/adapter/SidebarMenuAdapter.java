package com.upbrasil.up.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.upbrasil.up.R;
import com.upbrasil.up.model.Menu;

import java.util.ArrayList;

public class SidebarMenuAdapter extends ArrayAdapter<Menu> {
    public SidebarMenuAdapter(Context context, ArrayList<Menu> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Menu menu = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_menu, parent, false);
        }

        TextView tituloMenu = convertView.findViewById(R.id.titulo_menu);
        tituloMenu.setText(menu.getTitulo());

        return convertView;
    }
}
