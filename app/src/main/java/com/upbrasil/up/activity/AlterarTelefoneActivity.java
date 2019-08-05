package com.upbrasil.up.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irozon.sneaker.Sneaker;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.model.Usuario;

public class AlterarTelefoneActivity extends AppCompatActivity {
    ImageView botaoVoltar;
    TextView tituloNavbar;
    FirebaseUser user;
    private DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
    FirebaseAuth firebaseAuth;
    EditText campoTelefone;
    Button botaoSalvarTelefone;
    private DatabaseReference referenciaTelefone = FirebaseDatabase.getInstance().getReference("telefones");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_telefone);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        campoTelefone = findViewById(R.id.campo_alterar_telefone);

        botaoSalvarTelefone = findViewById(R.id.botao_alterar_telefone);
        botaoSalvarTelefone.setEnabled(false);
        botaoSalvarTelefone.setBackgroundResource(R.color.myGrey);
        botaoSalvarTelefone.setTextColor(getResources().getColor(R.color.myOtherGrey));

        tituloNavbar = findViewById(R.id.titulo_navbar);

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buscarInformacoes();

        botaoSalvarTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(campoTelefone.getText().toString().isEmpty()) {
                    mostrarErro("Erro", "É preciso preencher o campo telefone");
                } else {
                    verificarNumeroExiste();
                    Intent intent = new Intent(AlterarTelefoneActivity.this, ConfirmarTelefoneActivity.class);
                    intent.putExtra("telefone", campoTelefone.getText().toString());
                    intent.putExtra("cadastrado", true);
                    startActivity(intent);
                }
            }
        });

        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("(NN) NNNN-NNNNN");
        MaskTextWatcher maskPhone = new MaskTextWatcher(campoTelefone, simpleMaskFormatter);
        campoTelefone.addTextChangedListener( maskPhone );

        campoTelefone.addTextChangedListener(new TextWatcher() {
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
                    botaoSalvarTelefone.setEnabled(false);
                    botaoSalvarTelefone.setBackgroundResource(R.color.myGrey);
                    botaoSalvarTelefone.setTextColor(getResources().getColor(R.color.myOtherGrey));
                } else {
                    botaoSalvarTelefone.setEnabled(true);
                    botaoSalvarTelefone.setBackgroundResource(R.color.quantum_googgreen500);
                    botaoSalvarTelefone.setTextColor(Color.WHITE);
                }
            }
        });
    }

    public void buscarInformacoes() {
        referenciaUsuarios.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot issue : dataSnapshot.getChildren()){
                        Usuario usuario = issue.getValue(Usuario.class);

                        if(usuario.getTelefone() == null) {
                            tituloNavbar.setText("Adicionar Telefone");
                        } else {
                            tituloNavbar.setText("Alterar Telefone");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void mostrarErro(String titulo, String mensagem) {
        Sneaker.with(this) // Activity, Fragment or ViewGroup
                .setTitle(titulo)
                .setMessage(mensagem)
                .sneakError();
    }

    public void mostrarSucesso(String titulo, String mensagem) {
        Sneaker.with(this) // Activity, Fragment or ViewGroup
                .setTitle(titulo)
                .setMessage(mensagem)
                .sneakSuccess();
    }

    public void verificarNumeroExiste() {
        String id = referenciaTelefone.push().getKey();
        referenciaTelefone.child(id).child("telefone").setValue(campoTelefone.getText().toString());
    }
}
