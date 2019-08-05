package com.upbrasil.up.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.model.Usuario;

public class AlterarEmailActivity extends AppCompatActivity {
    ImageView botaoVoltar;
    EditText campoEmail;
    Button botaoAlterarEmail;

    private Loading loading;
    private Alerts alerts;

    private String txtEmail;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_email);

        loading = new Loading(this);
        alerts = new Alerts(this);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        campoEmail = findViewById(R.id.campo_mensagem);
        campoEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        botaoAlterarEmail = findViewById(R.id.botao_recuperar_senha);

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        botaoAlterarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.mostrarLoading();

                if(camposPreenchidos()) {
                    Usuario usuario = new Usuario();
                    usuario.atualizaEmail(user, AlterarEmailActivity.this, loading, txtEmail);
                } else {
                    loading.fecharLoading();
                }
            }
        });

    }



    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public boolean camposPreenchidos() {
        txtEmail = campoEmail.getText().toString();

        if(txtEmail.isEmpty()) {
            alerts.mostrarPopupErro("O campo email precisa ser preenchido");
            return false;
        }

        if(!isValidEmail(txtEmail)) {
            alerts.mostrarPopupErro("O email precisa ser válido");
            return false;
        }

        return true;
    }
}
