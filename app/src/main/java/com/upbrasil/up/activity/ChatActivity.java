package com.upbrasil.up.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.adapter.MensagensAdapter;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.model.Mensagem;
import com.upbrasil.up.model.Requisicao;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    ImageView botaoVoltar;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("requisicoes");
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ArrayList<Mensagem> listaMensagens;
    MensagensAdapter adapter;
    ListView listView;
    EditText campoMensagem;
    ImageButton botaoEnviarMensagem;
    Requisicao requisicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        listaMensagens = new ArrayList<Mensagem>();
        listView = (ListView) findViewById(R.id.lista_mensagens);

        campoMensagem = findViewById(R.id.campo_mensagem);
        botaoEnviarMensagem = findViewById(R.id.envia_mensagem);

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buscarMensagens();

        botaoEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensagem();
            }
        });
    }

    public void buscarMensagens() {
        databaseReference.orderByChild("status").equalTo("aguardando").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaMensagens.clear();

                if(dataSnapshot.exists()) {
                    adapter = new MensagensAdapter(ChatActivity.this, listaMensagens);

                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        requisicao = each.getValue(Requisicao.class);

                        if(requisicao.getPassageiro().getId().equals(user.getUid())) {
                            for(DataSnapshot msg : each.child("mensagens").getChildren()) {
                                listaMensagens.add(msg.getValue(Mensagem.class));
                            }
                        }
                    }

                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void enviarMensagem() {
        Mensagem mensagem = new Mensagem();
        mensagem.setId(databaseReference.push().getKey());

        if(requisicao != null) {
            if(campoMensagem.getText().toString().isEmpty()) {
                mostrarConfirmacao("Você precisa digitar alguma mensagem");
            } else {
                mensagem.setMensagem(campoMensagem.getText().toString());
                if(requisicao.getPassageiro().getId().equals(user.getUid())) {
                    mensagem.setIdPassageiro(user.getUid());
                    mensagem.setNomePassageiro(user.getDisplayName());
                } else {
                    mensagem.setIdMotorista(user.getUid());
                    mensagem.setNomeMotorista(user.getDisplayName());
                }
                databaseReference.child(requisicao.getId()).child("mensagens").child(mensagem.getId()).setValue(mensagem);
                campoMensagem.getText().clear();
                adapter.notifyDataSetChanged();
                listView.invalidateViews();
                buscarMensagens();
            }
        }

    }

    public void finalizaChat() {

    }

    public void mostrarConfirmacao(String mensagem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ChatActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setMessage(mensagem)
                .setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.show();
    }
}
