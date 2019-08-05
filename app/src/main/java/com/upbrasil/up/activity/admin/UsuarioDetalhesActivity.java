package com.upbrasil.up.activity.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.model.Usuario;

public class UsuarioDetalhesActivity extends AppCompatActivity {
    private ImageView botaoVoltar;
    private TextView tituloNavbar, nome, email;
    private ImageView imagemPerfilAprovado, imagemTipo;
    private Button btnAprovar, btnRemover;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_detalhes);

        Intent intent = getIntent();
        final String idUsuario = intent.getStringExtra("id_usuario");

        imagemPerfilAprovado = findViewById(R.id.imagem_perfil_verificado);
        imagemTipo = findViewById(R.id.imagem_tipo_usuario);

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tituloNavbar = findViewById(R.id.titulo_navbar);

        nome = findViewById(R.id.nome_usuario);
        email = findViewById(R.id.email_usuario);

        btnAprovar = findViewById(R.id.btn_aprovar);
        btnRemover = findViewById(R.id.btn_deletar);

        buscarInformacoes(idUsuario);

        btnAprovar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(idUsuario).child("aprovado").setValue(true);
                finish();
            }
        });

        btnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.child(idUsuario).removeValue();
                finish();
            }
        });
    }

    public void buscarInformacoes(String idUsuario) {
        reference.orderByChild("id").equalTo(idUsuario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        Usuario usuario = each.getValue(Usuario.class);
                        tituloNavbar.setText(usuario.getNomeCompleto());

                        if(usuario.getTipo().equals("P")) {
                            imagemTipo.setImageResource(R.drawable.passenger);
                        }

                        if(usuario.getAprovado() != null) {
                            if(usuario.getAprovado()) {
                                imagemPerfilAprovado.setVisibility(View.VISIBLE);
                                btnAprovar.setVisibility(View.GONE);
                            } else {
                                imagemPerfilAprovado.setVisibility(View.GONE);
                                btnAprovar.setVisibility(View.VISIBLE);
                            }
                        }

                        if(usuario.getNomeCompleto() != null) {
                            nome.setText(usuario.getNomeCompleto());
                        }

                        if(usuario.getEmail() != null) {
                            email.setText(usuario.getEmail());
                        }
                    }
                } else {
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
