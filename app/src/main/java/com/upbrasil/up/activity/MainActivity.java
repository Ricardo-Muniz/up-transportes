package com.upbrasil.up.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upbrasil.up.R;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.helper.Permissions;
import com.upbrasil.up.helper.Permissoes;
import com.upbrasil.up.helper.UsuarioFirebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private TextView titulo;

    private Button botao;

    private EditText telefone;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private LoginButton botaoFacebook;

    private static final String EMAIL = "email";

    private CallbackManager callbackManager;

    private FirebaseAuth mAuth;

    private Button btnEsqueci;

    private Alerts alerts;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
    };

    Loading loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        alerts = new Alerts(this);
        loading = new Loading(this);

        Permissoes.validarPermissoes(permissoes, this, 1);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.upbrasil.up",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {
            alerts.mostrarPopupErro("Ocorreu um erro específico, por entre em contato conosco " + e.getStackTrace().toString());
        } catch (NoSuchAlgorithmException e) {
            alerts.mostrarPopupErro("Ocorreu um erro específico, por entre em contato conosco " + e.getStackTrace().toString());
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        titulo = findViewById(R.id.titulo_escolha);
        titulo.setText(Html.fromHtml("Qual o seu <b>" + "número <br> de telefone" + "</b>"));

        botao = findViewById(R.id.botao);
        botao.setEnabled(false);

        callbackManager = CallbackManager.Factory.create();
        botaoFacebook = (LoginButton) findViewById(R.id.login_button);
        botaoFacebook.setReadPermissions(Arrays.asList(EMAIL));
        botaoFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                loading.mostrarLoading();
                buscaUsuarioFacebook(loginResult);
            }

            @Override
            public void onCancel() {
                alerts.mostrarPopupErro("Requisição cancelada");
            }

            @Override
            public void onError(FacebookException exception) {
                alerts.mostrarPopupErro("Ocorreu um erro específico, por entre em contato conosco " + exception.getStackTrace().toString());
            }
        });

        liberarBotaoTelefone();

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.mostrarLoading();
                Intent intent = new Intent(MainActivity.this, ConfirmarTelefoneActivity.class);
                intent.putExtra("telefone", telefone.getText().toString());
                intent.putExtra("cadastrado", false);
                startActivity(intent);
                finish();
                loading.fecharLoading();
            }
        });

        btnEsqueci = findViewById(R.id.btn_esqueci);

        btnEsqueci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.mostrarLoading();
                Intent intent = new Intent(MainActivity.this, EsqueciSenhaActivity.class);
                startActivity(intent);
                loading.fecharLoading();
            }
        });

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
    }

    public void adicionaBackgroundBotao(boolean condicao) {
        if(condicao) {
            botao.setEnabled(false);
            botao.setBackgroundResource(R.color.myGrey);
            botao.setTextColor(Color.BLACK);
        } else {
            botao.setEnabled(true);
            botao.setBackgroundResource(R.drawable.degrade_color);
            botao.setTextColor(Color.WHITE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                // Check if the only required permission has been granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("Permission", "Contact permission has now been granted. Showing result.");
                    Toast.makeText(this,"Contact Permission is Granted",Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.this.finish();
                    System.exit(0);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token, final String nome, final String email) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            UsuarioFirebase.redirecionaUsuarioLogadoFacebook(MainActivity.this, nome, email, mAuth.getUid());
                        } else {
                            loading.fecharLoading();
                            alerts.mostrarPopupErro("Ocorreu um erro específico, por entre em contato conosco " + task.getException().toString());
                        }

                    }
                });
    }

    public void buscaUsuarioFacebook(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        try {
                            String name=object.getString("name");
                            String email=object.getString("email");
                            String id=object.getString("id");

                            if (Permissions.Check_FINE_LOCATION(MainActivity.this)) {
                                handleFacebookAccessToken(loginResult.getAccessToken(), name, email);
                            }


                        } catch (JSONException e) {
                            loading.fecharLoading();
                            alerts.mostrarPopupErro("Ocorreu um erro específico, por entre em contato conosco " + e.getStackTrace().toString());
                        }

                    }
                });



        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender, birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void liberarBotaoTelefone() {
        telefone = findViewById(R.id.telefone);
        telefone.setEnabled(true);
        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("(NN) NNNN-NNNNN");
        MaskTextWatcher maskPhone = new MaskTextWatcher(telefone, simpleMaskFormatter);
        telefone.addTextChangedListener( maskPhone );
        telefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();

                if (value.length() <= 13) {
                    adicionaBackgroundBotao(true);
                } else {
                    adicionaBackgroundBotao(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
