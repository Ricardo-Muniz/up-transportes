package com.upbrasil.up.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.helper.UsuarioFirebase;

public class ConfirmarPerfilActivity extends AppCompatActivity {
    private EditText campoEmail;
    private EditText campoSenha;

    private Button botaoEntrar;

    private Alerts alerts;
    private Loading loading;

    private String txtEmail;
    private String txtSenha;

    private ImageView btnVoltar;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_perfil);

        alerts = new Alerts(this);
        loading = new Loading(this);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        btnVoltar = findViewById(R.id.botao_voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmarPerfilActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        campoEmail = findViewById(R.id.campo_email);
        campoSenha = findViewById(R.id.campo_senha);

        campoEmail.setEnabled(false);
        campoEmail.setText(email);

        botaoEntrar = findViewById(R.id.botao_entrar);
        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loading.mostrarLoading();

                if(camposPreenchidos()) {
                    logarUsuario();
                } else {
                    loading.fecharLoading();
                }
            }
        });
    }

    public void logarUsuario(){
        FirebaseAuth autenticacao;
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                txtEmail, txtSenha
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){
                    UsuarioFirebase.redirecionaUsuarioLogado(ConfirmarPerfilActivity.this);
                    loading.fecharLoading();
                } else {
                    String excecao;

                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }

                    alerts.mostrarPopupErro(excecao);
                    loading.fecharLoading();
                }
            }
        });

    }

    public boolean camposPreenchidos() {
        txtEmail = campoEmail.getText().toString();
        txtSenha = campoSenha.getText().toString();

        if(txtEmail.isEmpty()) {
            alerts.mostrarPopupErro("O email precisa ser preenchido");
            return false;
        }

        if(!isValidEmail(txtEmail)) {
            alerts.mostrarPopupErro("O email é inválido");
            return false;
        }

        if(txtSenha.isEmpty()) {
            alerts.mostrarPopupErro("A senha precisa ser preenchida");
            return false;
        }

        return true;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ConfirmarPerfilActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
