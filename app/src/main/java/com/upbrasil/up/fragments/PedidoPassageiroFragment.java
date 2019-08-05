package com.upbrasil.up.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.upbrasil.up.R;
import com.upbrasil.up.activity.PassageiroActivity;
import com.upbrasil.up.model.Requisicao;


public class PedidoPassageiroFragment extends Fragment  {
    private Requisicao requisicao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_pedido_passageiro,
                container, false);

        requisicao = (Requisicao) getArguments().getSerializable("requisicao");

        ImageButton btnFechar = view.findViewById(R.id.botao_fechar);
        Button btnConfirmarCorrida = view.findViewById(R.id.botao_confirmar_pedido);

        TextView tvEnderecoPartida = view.findViewById(R.id.rua_partida);
        TextView tvEnderecoDestino = view.findViewById(R.id.rua_destino);
        TextView tvFormaPagamento = view.findViewById(R.id.forma_pagamento);
        TextView tvPreco = view.findViewById(R.id.valor_corrida);



        tvEnderecoPartida.setText(enderecoPartida());
        tvEnderecoDestino.setText(enderecoDestino());
        tvFormaPagamento.setText(requisicao.getFormaPagamento());
        tvPreco.setText(valorCorrida());

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btnChamarUber = (Button) PassageiroActivity.passageiroActivity.findViewById(R.id.buttonChamarUber);
                btnChamarUber.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(PedidoPassageiroFragment.this).commit();
            }
        });

        btnConfirmarCorrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PassageiroActivity.passageiroActivity.salvarRequisicao();
                Button btnChamarUber = (Button) PassageiroActivity.passageiroActivity.findViewById(R.id.buttonChamarUber);
                btnChamarUber.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().beginTransaction().remove(PedidoPassageiroFragment.this).commit();
            }
        });


        return view;
    }

    private String valorCorrida() {
        String valor = new String();
        valor = requisicao.getPreco().replace(".", ",");
        return "R$ "  + valor;
    }

    private StringBuilder enderecoPartida() {
        StringBuilder enderecoPartida = new StringBuilder();

        if(requisicao.getPartida().getRua() != null) {
            enderecoPartida.append(requisicao.getPartida().getRua());
        }

        if(!requisicao.getPartida().getRua().equals(requisicao.getPartida().getNumero())) {
            if(requisicao.getPartida().getNumero() != null) {
                enderecoPartida.append(", " + requisicao.getPartida().getNumero());
            }
        }

        if(requisicao.getPartida().getBairro() != null) {
            enderecoPartida.append(", " + requisicao.getPartida().getBairro());
        }

        if(requisicao.getPartida().getCidade() != null) {
            enderecoPartida.append(", " + requisicao.getPartida().getCidade());
        }

        if(requisicao.getPartida().getCep() != null) {
            enderecoPartida.append(" - " + requisicao.getPartida().getCep());
        }

        return enderecoPartida;
    }

    private StringBuilder enderecoDestino() {
        StringBuilder enderecoDestino = new StringBuilder();

        if(requisicao.getDestino().getRua() != null) {
            enderecoDestino.append(requisicao.getDestino().getRua());
        }

        if(requisicao.getDestino().getNumero() != null) {
            enderecoDestino.append(", " + requisicao.getDestino().getNumero());
        }

        if(requisicao.getDestino().getBairro() != null) {
            enderecoDestino.append(", " + requisicao.getDestino().getBairro());
        }

        if(requisicao.getDestino().getCidade() != null) {
            enderecoDestino.append(", " + requisicao.getDestino().getCidade());
        }

        if(requisicao.getDestino().getCep() != null) {
            enderecoDestino.append(" - " + requisicao.getDestino().getCep());
        }

        return enderecoDestino;
    }
}
