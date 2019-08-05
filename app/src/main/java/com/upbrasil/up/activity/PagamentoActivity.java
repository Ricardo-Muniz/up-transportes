package com.upbrasil.up.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.model.FormasPagamento;
import com.upbrasil.up.model.Usuario;

import java.util.ArrayList;

public class PagamentoActivity extends AppCompatActivity {
    private ImageView botaoVoltar;
    FirebaseUser user;
    DatabaseReference firebaseDatabase;
    FirebaseAuth firebaseAuth;
    Button btnPaypal, btnDinheiro, btnDebito, btnCredito;
    ArrayList<Button> btnArray = new ArrayList<Button>();
    Usuario usuario;

    private Alerts alerts;

    private Boolean isDinheiro = false, isPaypal = false, isDebito = false, isCredito = false;

    Button btnLimpaPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamento);

        alerts = new Alerts(this);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        btnPaypal = findViewById(R.id.ativar_paypal2);
        btnDinheiro = findViewById(R.id.ativar_dinheiro);
        btnDebito = findViewById(R.id.ativar_debito);
        btnCredito = findViewById(R.id.ativar_credito);

        btnLimpaPagamento = findViewById(R.id.limpar_pagamentos);

        btnArray.add(btnPaypal);
        btnArray.add(btnDinheiro);
        btnArray.add(btnDebito);
        btnArray.add(btnCredito);

        buscarFormaPagamento();
        buscarFormasPagamento();


        btnDinheiro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaLayout(btnDinheiro);
                usuario.setFormaPagamento(usuario.DINHEIRO);

                if(usuario.getTipo().equals("P")) {
                    usuario.salvarPagamento();
                    alerts.mostrarPopupSucessoSemRedirect("Sua forma de pagamento foi atualizada com sucesso");

                    isDinheiro = true;
                } else {
                    if(!isDinheiro) {
                        usuario.salvarPagamento();
                        alerts.mostrarPopupSucessoSemRedirect("Sua forma de pagamento foi atualizada com sucesso");
                        isDinheiro = true;
                    }
                }
            }
        });

        btnPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(usuario.getTipo().equals("P")) {
                    atualizaLayout(btnPaypal);
                    Intent intent = new Intent(PagamentoActivity.this, AdicionarMetodoPagamentoActivity.class);
                    startActivity(intent);
                } else {
                    atualizaLayout(btnPaypal);
                    usuario.setFormaPagamento(usuario.PAYPAL);

                    if(!isPaypal) {
                        usuario.salvarPagamento();
                        alerts.mostrarPopupSucessoSemRedirect("Sua forma de pagamento foi atualizada com sucesso");
                        isPaypal = true;
                    }
                }
            }
        });

        btnDebito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaLayout(btnDebito);
                usuario.setFormaPagamento(usuario.DEBITO);

                if(usuario.getTipo().equals("P")) {
                    usuario.salvarPagamento();
                    alerts.mostrarPopupSucessoSemRedirect("Sua forma de pagamento foi atualizada com sucesso");
                    isDebito = true;
                } else {
                    if(!isDebito) {
                        usuario.salvarPagamento();
                        alerts.mostrarPopupSucessoSemRedirect("Sua forma de pagamento foi atualizada com sucesso");
                        isDebito = true;
                    }
                }
            }
        });

        btnCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                atualizaLayout(btnCredito);
                usuario.setFormaPagamento(usuario.CREDITO);

                if(usuario.getTipo().equals("P")) {
                    usuario.salvarPagamento();
                    alerts.mostrarPopupSucessoSemRedirect("Sua forma de pagamento foi atualizada com sucesso");
                    isCredito = true;
                } else {
                    if(!isCredito) {
                        usuario.salvarPagamento();
                        alerts.mostrarPopupSucessoSemRedirect("Sua forma de pagamento foi atualizada com sucesso");
                        isCredito = true;
                    }
                }
            }
        });

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnLimpaPagamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase = ConfiguracaoFirebase.getFirebaseDatabase();
                firebaseDatabase.child("usuarios").child(user.getUid()).child("formasPagamento").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            alerts.mostrarPopupSucesso("Suas formas de pagamento foram reniciadas com sucesso");
                        }
                    }
                });
            }
        });
    }

    public void buscarFormasPagamento() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseDatabase.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        usuario = each.getValue(Usuario.class);

                        if(usuario.getTipo().equals("M")) {
                            if(usuario.getFormasPagamento() != null) {
                                for(DataSnapshot formas : each.child("formasPagamento").getChildren()) {
                                    FormasPagamento formasPagamento = formas.getValue(FormasPagamento.class);

                                    if(formasPagamento.getNome().equals("DINHEIRO")) {
                                        adicionaBackgroundBotao(btnDinheiro);
                                        isDinheiro = true;
                                    }

                                    if(formasPagamento.getNome().equals("PAYPAL")) {
                                        adicionaBackgroundBotao(btnPaypal);
                                        isPaypal = true;
                                    }

                                    if(formasPagamento.getNome().equals("DEBITO")) {
                                        adicionaBackgroundBotao(btnDebito);
                                        isDebito = true;
                                    }

                                    if(formasPagamento.getNome().equals("CREDITO")) {
                                        adicionaBackgroundBotao(btnCredito);
                                        isCredito = true;
                                    }
                                }
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void buscarFormaPagamento() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
        firebaseDatabase.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        usuario = each.getValue(Usuario.class);

                        if(usuario.getTipo().equals("P")) {
                            btnLimpaPagamento.setVisibility(View.GONE);

                            if(usuario.getFormaPagamento() != null) {
                                if(usuario.getFormaPagamento().equals("DINHEIRO")) {
                                    adicionaBackgroundBotao(btnDinheiro);
                                } else if(usuario.getFormaPagamento().equals("PAYPAL")) {
                                    adicionaBackgroundBotao(btnPaypal);
                                } else if(usuario.getFormaPagamento().equals("DEBITO")) {
                                    adicionaBackgroundBotao(btnDebito);
                                } else if(usuario.getFormaPagamento().equals("CREDITO")) {
                                    adicionaBackgroundBotao(btnCredito);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void atualizaLayout(Button botao) {
        if(usuario.getTipo().equals("P")) {
            for(Button btn : btnArray) {
                btn.setBackgroundColor(Color.TRANSPARENT);
                btn.setTextColor(Color.BLACK);
            }

            botao.setBackgroundResource(R.drawable.degrade_color);
            botao.setTextColor(Color.WHITE);
        } else {
            botao.setBackgroundResource(R.drawable.degrade_color);
            botao.setTextColor(Color.WHITE);
        }
    }

    public void adicionaBackgroundBotao(Button button) {
        button.setBackgroundResource(R.drawable.degrade_color);
        button.setTextColor(Color.WHITE);
    }

    public void removeLayout(Button botao) {
        botao.setBackgroundColor(Color.TRANSPARENT);
        botao.setTextColor(Color.BLACK);
    }
}
