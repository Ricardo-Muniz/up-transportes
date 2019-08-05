package com.upbrasil.up.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlterarSenhaActivity extends AppCompatActivity {
    private ImageView botaoVoltar;
    private Button botaoAlterarSenha;

    private EditText campoSenha;
    private EditText campoConfirmaSenha;

    private String txtSenha;
    private String txtConfirmaSenha;

    private FirebaseAuth autenticacao;
    private FirebaseUser usuario;

    Alerts alerts;
    Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_senha);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuario = autenticacao.getCurrentUser();

        loading = new Loading(this);
        alerts = new Alerts(this);

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        campoSenha = findViewById(R.id.campo_alterar_senha);
        campoConfirmaSenha = findViewById(R.id.campo_confirmar_senha);

        botaoAlterarSenha = findViewById(R.id.botao_alterar_senha);
        botaoAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.mostrarLoading();

                if(camposPreenchidos()) {
                    usuario.updatePassword(campoSenha.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()) {
                                loading.fecharLoading();
                                alerts.mostrarPopupSucessoSemRedirect("Sua senha foi alterada com sucesso");
                            } else {
                                loading.fecharLoading();

                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    alerts.mostrarPopupErro("Sua senha está extremamente fraca");
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    alerts.mostrarPopupErro("Suas credentiais são inválidas");
                                } catch(FirebaseAuthUserCollisionException e) {
                                    alerts.mostrarPopupErro("Houve um conflito entre usuários");
                                } catch (FirebaseAuthRecentLoginRequiredException e) {
                                    alerts.mostrarPopupErro("Sua sessão expirou, é necessário efetuar o login novamente.");

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            LoginManager.getInstance().logOut();
                                            FirebaseAuth firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
                                            firebaseAuth.signOut();
                                            Intent intent = new Intent(AlterarSenhaActivity.this, ConfirmarPerfilActivity.class);
                                            startActivity(intent);
                                        }
                                    }, 2000);
                                } catch(Exception e) {
                                    alerts.mostrarPopupErro(e.getMessage());
                                }
                            }
                        }
                    });
                } else {
                    loading.fecharLoading();
                }
            }
        });
    }

    public boolean camposPreenchidos() {
        txtSenha = campoSenha.getText().toString();
        txtConfirmaSenha = campoConfirmaSenha.getText().toString();

        if(txtConfirmaSenha.isEmpty() || txtSenha.isEmpty()) {
            alerts.mostrarPopupErro("Todos campos precisam ser preenchidos");
            return false;
        }

        if(!txtSenha.equals(txtConfirmaSenha)) {
            alerts.mostrarPopupErro("As senhas precisam ser iguais");
            return false;
        }

        if(!isValidPassword(txtSenha) || !isValidPassword(txtConfirmaSenha)) {
            alerts.mostrarPopupErro("Sua senha precisa ter no mínimo uma letra, um número, um caractere especial e 8 caracteres.");
            return false;
        }

        return true;
    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }
}
