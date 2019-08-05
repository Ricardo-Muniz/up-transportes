package com.upbrasil.up.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.upbrasil.up.R;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.model.Usuario;

public class EsqueciSenhaActivity extends AppCompatActivity {
    private ImageView botaoVoltar;

    private EditText etEmail;

    private String txtEmail;

    private Button btnRecuperarSenha;

    private FirebaseAuth firebaseAuth;

    private FirebaseUser user;

    private DatabaseReference firebaseDatabase;

    private Alerts alerts;

    Usuario usuario;

    private Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esqueci_senha);

        alerts = new Alerts(this);
        loading = new Loading(this);

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etEmail = findViewById(R.id.campo_email);
        btnRecuperarSenha = findViewById(R.id.botao_enviar);

        btnRecuperarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(camposPreenchidos()) {
                    loading.mostrarLoading();
                    usuario = new Usuario();
                    usuario.recuperarSenha(txtEmail, EsqueciSenhaActivity.this);
                    alerts.mostrarPopupSucessoSemRedirect("Enviamos um email para sua caixa de entrada, por favor verifique seu email");
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean camposPreenchidos() {
        txtEmail = etEmail.getText().toString();

        if(!isValidEmail(txtEmail)) {
            alerts.mostrarPopupErro("Digite um email válido");
            return false;
        }

        if(txtEmail.isEmpty()) {
            alerts.mostrarPopupErro("Precisamos saber qual o email da conta");
            return false;
        }

        if(!txtEmail.isEmpty() && isValidEmail(txtEmail)) {
            return true;
        }

        return false;
    }
}
