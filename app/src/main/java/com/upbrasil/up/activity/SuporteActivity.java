package com.upbrasil.up.activity;

import android.os.Bundle;
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
import com.upbrasil.up.model.Ticket;
import com.upbrasil.up.model.Usuario;

public class SuporteActivity extends AppCompatActivity {
    private ImageView btnVoltar;

    private EditText etMotivo;
    private EditText etMensagem;

    private String txtMotivo;
    private String txtMensagem;

    private Button btnEnviar;

    private Alerts alerts;

    private Ticket ticket;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suporte);

        alerts = new Alerts(this);
        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        usuario.setId(user.getUid());
        usuario.setNomeCompleto(user.getDisplayName());
        usuario.setEmail(user.getEmail());

        etMotivo = findViewById(R.id.campo_motivo);
        etMensagem = findViewById(R.id.campo_mensagem);

        btnEnviar = findViewById(R.id.botao_enviar);
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(camposPreenchidos()) {
                    ticket = new Ticket();
                    ticket.setId(ticket.gerarId());
                    ticket.setUsuario(usuario);
                    ticket.setData(ticket.pegarHorarioAtual());
                    ticket.setMotivo(txtMotivo);
                    ticket.setMensagem(txtMensagem);
                    ticket.salvar();

                    alerts.mostrarPopupSucesso("Sua mensagem foi enviada com sucesso! Responderemos o mais rápido possível");
                }
            }
        });

        btnVoltar = findViewById(R.id.botao_voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public boolean camposPreenchidos() {
        txtMotivo = etMotivo.getText().toString();
        txtMensagem = etMensagem.getText().toString();

        if(txtMotivo.isEmpty()) {
            alerts.mostrarPopupErro("Precisamos saber qual o motivo da sua mensagem");
            return false;
        }

        if(txtMensagem.isEmpty()) {
            alerts.mostrarPopupErro("Precisamos saber qual é a sua mensagem");
            return false;
        }

        if(!txtMensagem.isEmpty() && !txtMotivo.isEmpty()) {
            return true;
        }

        return false;
    }


}
