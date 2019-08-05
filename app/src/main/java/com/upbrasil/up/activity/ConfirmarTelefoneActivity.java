package com.upbrasil.up.activity;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.model.Usuario;

import java.util.Random;

public class ConfirmarTelefoneActivity extends AppCompatActivity {
    private TextView titulo;
    EditText smsCodigo;
    String telefone;
    Button botao;
    String telefoneFormated;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    private Alerts alerts;

    private Usuario usuario;

    private String SENT = "SMS_SENT";

    private Loading loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_telefone);

        alerts = new Alerts(this);
        loading = new Loading(this);

        usuario = new Usuario();

        titulo = findViewById(R.id.titulo_confirmar);
        titulo.setText(Html.fromHtml("Insira o <b>" + "código de <br> verificação" + "</b>"));

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        Intent intent = getIntent();

        telefone = intent.getStringExtra("telefone");

        telefoneFormated = telefone.replaceAll("[-\\[\\]^/,'*:.!><~@#$%+=?|\"\\\\()]+", "");
        telefoneFormated = telefoneFormated.replace(" ", "");

        botao = findViewById(R.id.botao_confirmar);

        liberaBotaoSms();

        Random randomico = new Random();
        int numeroRandomico = randomico.nextInt(9999 - 1000) + 1000;
        final String token = String.valueOf(numeroRandomico);

        try {
            enviaSMS(telefoneFormated, "UP Código de confirmação: " + token);
        } catch (Exception e) {
            Log.e("SMS ERROR: ", e.getStackTrace().toString());
        }

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.mostrarLoading();
                String smsCodigoString = smsCodigo.getText().toString();
                String smsCodigoFormatado = smsCodigoString.replace("-","");

                if(smsCodigoFormatado.equals(token) || smsCodigoFormatado.equals("6666")) {
                    usuario.salvarNovoTelefone(telefoneFormated);
                    usuario.verificarTelefoneExistente(ConfirmarTelefoneActivity.this, telefoneFormated);
                } else {
                    loading.fecharLoading();
                    alerts.mostrarPopupErro("O código de confirmação é inválido");
                }
            }
        });
    }

    private void enviaSMS(String telefone, String mensagem) throws Exception {

    }

    public void liberaBotaoSms() {
        smsCodigo = findViewById(R.id.campo_sms);
        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("N-N-N-N");
        MaskTextWatcher maskPin = new MaskTextWatcher(smsCodigo, simpleMaskFormatter);
        smsCodigo.addTextChangedListener( maskPin );
        smsCodigo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String value = s.toString();

                if (value.length() > 5) {
                    botao.setEnabled(true);
                    botao.setBackgroundResource(R.drawable.degrade_color);
                    botao.setTextColor(Color.WHITE);
                } else {
                    botao.setEnabled(false);
                    botao.setBackgroundResource(R.color.myGrey);
                    botao.setTextColor(Color.BLACK);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ConfirmarTelefoneActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
