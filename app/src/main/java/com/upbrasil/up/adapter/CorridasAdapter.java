package com.upbrasil.up.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.upbrasil.up.R;
import com.upbrasil.up.model.Requisicao;

import java.util.ArrayList;

public class CorridasAdapter extends ArrayAdapter<Requisicao> {
    TextView cidade, ruaDestino, status, preco;

    public CorridasAdapter(Context context, ArrayList<Requisicao> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Requisicao requisicao = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_corrida, parent, false);
        }

        TextView enderecoPartida = convertView.findViewById(R.id.endereco_partida);
        TextView enderecoChegada = convertView.findViewById(R.id.endereco_chegada);
        TextView valorCorrida = convertView.findViewById(R.id.valor);
        TextView nomeMotorista = convertView.findViewById(R.id.nome_motorista);
        ImageView formaPagamento = convertView.findViewById(R.id.forma_pagamento);

        if(requisicao.getMotorista() != null) {
            nomeMotorista.setText(requisicao.getMotorista().getNomeCompleto());
        }

        if(requisicao.getFormaPagamento() != null) {
            if(requisicao.getFormaPagamento().equals("paypal")) {
                formaPagamento.setImageResource(R.drawable.paypal);
            } else if(requisicao.getFormaPagamento().equals("dinheiro")) {
                formaPagamento.setImageResource(R.drawable.money);
            } else if(requisicao.getFormaPagamento().equals("debito") || requisicao.getFormaPagamento().equals("credito")) {
                formaPagamento.setImageResource(R.drawable.icone_cartao);
            }
        }


        if (requisicao.getPreco() != null) {
            valorCorrida.setText("R$" + requisicao.getPreco());
        }

        if(requisicao.getDestino().getCidade() != null) {
            if(requisicao.getDestino().getRua() != null) {
                StringBuilder rua = new StringBuilder();

                if(requisicao.getDestino().getRua() != null) {
                    rua.append(requisicao.getDestino().getRua());
                }

                if(requisicao.getDestino().getNumero() != null) {
                    rua.append(", " + requisicao.getDestino().getNumero());
                }

                if(requisicao.getDestino().getCidade() != null) {
                    rua.append(", " + requisicao.getDestino().getCidade());
                }

                if(requisicao.getDestino().getBairro() != null) {
                    rua.append(", " + requisicao.getDestino().getBairro());
                }

                enderecoChegada.setText(rua);
            }
        }

        if(requisicao.getPartida() != null) {
            StringBuilder rua = new StringBuilder();

            if(requisicao.getPartida().getRua() != null) {
                rua.append(requisicao.getPartida().getRua());
            }

            if(requisicao.getPartida().getNumero() != null) {
                rua.append(", " + requisicao.getPartida().getNumero());
            }

            if(requisicao.getPartida().getCidade() != null) {
                rua.append(", " + requisicao.getPartida().getCidade());
            }

            if(requisicao.getPartida().getBairro() != null) {
                rua.append(", " + requisicao.getPartida().getBairro());
            }


            enderecoPartida.setText(rua);
        }

        return convertView;
    }
}