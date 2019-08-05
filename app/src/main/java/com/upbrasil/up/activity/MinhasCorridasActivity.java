package com.upbrasil.up.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.adapter.CorridasAdapter;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.model.Requisicao;

import java.util.ArrayList;

public class MinhasCorridasActivity extends AppCompatActivity {
    ImageView botaoVoltar;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("requisicoes");

    private FirebaseAuth autenticacao;
    FirebaseUser user;

    ArrayList<Requisicao> listaRequisicoes;
    CorridasAdapter adapter;
    ListView listView;
    int contaClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_corridas);

        contaClick = 0;

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = autenticacao.getCurrentUser();

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buscarCorridas();

        listaRequisicoes = new ArrayList<Requisicao>();
        listView = (ListView) findViewById(R.id.lista_corridas);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout layout = view.findViewById(R.id.detalhes_corrida);
                layout.setVisibility(View.VISIBLE);

                if(contaClick > 0) {
                    layout.setVisibility(View.GONE);
                    contaClick = 0;
                } else if(contaClick == 0) {
                    layout.setVisibility(View.VISIBLE);
                    contaClick = 1;
                }
            }
        });
    }

    public void buscarCorridas() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaRequisicoes.clear();

                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        Requisicao requisicao = each.getValue(Requisicao.class);
                        adapter = new CorridasAdapter(MinhasCorridasActivity.this, listaRequisicoes);

                        if(requisicao.getPassageiro().getId().equals(user.getUid())) {
                            if(requisicao.getStatus().equals(requisicao.STATUS_ENCERRADA) || requisicao.getStatus().equals(requisicao.STATUS_FINALIZADA)) {
                                listaRequisicoes.add(requisicao);
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

}
