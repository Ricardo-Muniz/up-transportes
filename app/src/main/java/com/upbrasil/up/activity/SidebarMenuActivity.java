package com.upbrasil.up.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.activity.admin.HomeActivity;
import com.upbrasil.up.adapter.SidebarMenuAdapter;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.model.Menu;
import com.upbrasil.up.model.Usuario;

import java.util.ArrayList;

public class SidebarMenuActivity extends AppCompatActivity {
    ImageView botaoVoltar;
    Menu minhasCorridas, pagamento, motorista, configuracao, admin, faleConosco, sair;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("usuarios");
    ArrayList<Menu> arrayOfMenus;
    ImageView fotoPerfil;
    TextView nomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sidebar_menu);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        fotoPerfil = findViewById(R.id.perfil_usuario);
        nomeUsuario = findViewById(R.id.nome_usuario);

        arrayOfMenus = new ArrayList<Menu>();

        minhasCorridas = new Menu("MINHAS CORRIDAS", R.drawable.brazil);
        arrayOfMenus.add(minhasCorridas);

        pagamento = new Menu("PAGAMENTO", R.drawable.brazil);
        arrayOfMenus.add(pagamento);

        configuracao = new Menu("INFORMAÇÕES PESSOAIS", R.drawable.brazil);
        arrayOfMenus.add(configuracao);

        faleConosco = new Menu("FALE CONOSCO", R.drawable.icone_suporte);
        arrayOfMenus.add(faleConosco);

        sair = new Menu("SAIR", R.drawable.icone_logout);
        arrayOfMenus.add(sair);

        SidebarMenuAdapter adapter = new SidebarMenuAdapter(this, arrayOfMenus);
        final ListView listView = (ListView) findViewById(R.id.lista_menu);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Menu menu = (Menu) listView.getItemAtPosition(position);
                redirecionar(menu);
            }
        });

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        buscarUsuario();
    }

    public void buscarUsuario() {
        firebaseDatabase.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot each : dataSnapshot.getChildren()) {
                        Usuario usuario = each.getValue(Usuario.class);

                        nomeUsuario.setText(usuario.getNomeCompleto());

                        if(usuario.getNivelUsuario() == 3) {
                            admin = new Menu("ADMINISTRADOR", R.drawable.brazil);
                            arrayOfMenus.add(admin);
                        }

                        if(usuario.getFotoPerfil() != null) {
                            Glide.with(SidebarMenuActivity.this).load(usuario.getFotoPerfil()).apply(RequestOptions.circleCropTransform()).into(fotoPerfil);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void redirecionar(Menu menu) {
        Intent intent = new Intent();

        if(admin != null) {
            if(menu.getTitulo().equals(admin.getTitulo())) {
                intent = new Intent(SidebarMenuActivity.this, HomeActivity.class);
            }
        }

        if(menu.getTitulo().equals(minhasCorridas.getTitulo())) {
            intent = new Intent(SidebarMenuActivity.this, MinhasCorridasActivity.class);
        } else if(menu.getTitulo().equals(pagamento.getTitulo())) {
            intent = new Intent(SidebarMenuActivity.this, PagamentoActivity.class);
        } else if(menu.getTitulo().equals(configuracao.getTitulo())) {
            intent = new Intent(SidebarMenuActivity.this, ConfiguracoesActivity.class);
        } else if(menu.getTitulo().equals(faleConosco.getTitulo())) {
            intent = new Intent(SidebarMenuActivity.this, SuporteActivity.class);
        } else if(menu.getTitulo().equals(sair.getTitulo())) {
            LoginManager.getInstance().logOut();
            firebaseAuth.signOut();
            intent = new Intent(SidebarMenuActivity.this, MainActivity.class);
        }

        startActivity(intent);
    }
}
