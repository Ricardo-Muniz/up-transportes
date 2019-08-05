package com.upbrasil.up.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.model.Mensagem;

import java.util.ArrayList;

public class MensagensAdapter extends ArrayAdapter<Mensagem> {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public MensagensAdapter(Context context, ArrayList<Mensagem> mensagens) {
        super(context, 0, mensagens);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Mensagem mensagem = getItem(position);
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();
        // Check if an existing view is being reused, otherwise inflate the view

        if (convertView == null) {
            if(mensagem.getIdPassageiro() != null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mensagem, parent, false);
            } else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mensagem_motorista, parent, false);
            }
        }

        TextView mensagemTexto = convertView.findViewById(R.id.mensagem_chat);
        mensagemTexto.setText(mensagem.getMensagem());

        return convertView;
    }
}