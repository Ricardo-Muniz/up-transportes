package com.upbrasil.up.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;

public class AdicionarMetodoPagamentoActivity extends AppCompatActivity {
    private ImageView botaoVoltar;
    private EditText campoEmailPaypal;
    private EditText campoSenhaPaypal;
    private Button botaoSalvarPaypal;
    DatabaseReference firebaseDatabase;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_metodo_pagamento);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        campoEmailPaypal = findViewById(R.id.campo_email);
        campoSenhaPaypal = findViewById(R.id.campo_alterar_senha);
        botaoSalvarPaypal = findViewById(R.id.botao_salvar_paypal);

        botaoSalvarPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(campoEmailPaypal.getText().toString().isEmpty()) {
                    mostrarConfirmacao("Preencha todos os campos");
                } else if(campoSenhaPaypal.getText().toString().isEmpty()) {
                    mostrarConfirmacao("Preencha todos os campos");
                } else if(!campoEmailPaypal.getText().toString().isEmpty() && !campoSenhaPaypal.getText().toString().isEmpty()) {
                    selecionarPaypal();
                    mostrarConfirmacao("Paypal salvo como forma de pagamento");
                    finish();
                }
            }
        });
    }


    public void selecionarPaypal() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseDatabase.child(user.getUid()).child("formaPagamento").setValue("paypal");
    }

    public void mostrarConfirmacao(String mensagem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(AdicionarMetodoPagamentoActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setMessage(mensagem)
                .setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.show();
    }
}
