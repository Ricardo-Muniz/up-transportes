package com.upbrasil.up.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.upbrasil.up.R;
import com.upbrasil.up.model.Usuario;

import java.util.ArrayList;

public class UsuariosAdapter extends ArrayAdapter<Usuario> implements Filterable {
    public UsuariosAdapter(Context context, ArrayList<Usuario> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Usuario usuario = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_usuario, parent, false);
        }

        TextView nome = convertView.findViewById(R.id.nome_usuario);
        TextView email = convertView.findViewById(R.id.email_usuario);
        ImageView tipo = convertView.findViewById(R.id.tipo_usuario);
        ImageView aprovado = convertView.findViewById(R.id.aprovado_usuario);

        if(usuario.getNomeCompleto() != null) {
            nome.setText(usuario.getNomeCompleto());
        }

        if(usuario.getEmail() != null) {
            email.setText(usuario.getEmail());
        }

        if(usuario.getTipo() != null) {
            if(usuario.getTipo().equals("P")) {
                aprovado.setVisibility(View.GONE);
                tipo.setImageResource(R.drawable.passenger);
            } else {
                tipo.setImageResource(R.drawable.icon);
            }
        }

        if(usuario.getAprovado() != null) {
            if(!usuario.getAprovado()) {
                aprovado.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public class ViewHolder {
        TextView name;
    }

}