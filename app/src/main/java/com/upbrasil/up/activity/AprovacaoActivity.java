package com.upbrasil.up.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.model.Usuario;

public class AprovacaoActivity extends AppCompatActivity {
    private TextView titulo, texto;
    Usuario usuario;
    private DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aprovacao);

        Intent i = getIntent();
        usuario = (Usuario) i.getSerializableExtra("usuario");

        titulo = findViewById(R.id.titulo_aprovacao);
        titulo.setText(Html.fromHtml("Cadastro <b> efetuado " + "<br>com sucesso " + "</b>"));

        texto = findViewById(R.id.texto_aprovacao);

        if(usuario.getTipo().equals("M")) {
            texto.setText("Olá Motorista " + usuario.getNomeCompleto() +", seu cadastro será analisado pela nossa equipe e respondido em breve via e-mail!");
            findViewById(R.id.botao_entrar).setVisibility(View.INVISIBLE);
        } else if (usuario.getTipo().equals("P")) {
            texto.setText("Olá Passageiro " + usuario.getNomeCompleto() +", seu cadastro foi efetuado com sucesso, clique no botão abaixo para começar a usar o UP!");
            findViewById(R.id.botao_entrar).setVisibility(View.VISIBLE);
        }

        salvarUsuario();
    }

    public void salvarUsuario() {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                usuario.setId(autenticacao.getUid());
                referenciaUsuarios.child(usuario.getId()).setValue(usuario);
            }
        });
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        finish();
    }
}
