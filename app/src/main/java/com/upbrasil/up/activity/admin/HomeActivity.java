package com.upbrasil.up.activity.admin;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upbrasil.up.R;
import com.upbrasil.up.adapter.UsuariosAdapter;
import com.upbrasil.up.model.Usuario;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private TextView nome, email, telefone, titulo_navbar;
    private ImageView carImagem, docImagem;
    private Button btnAprovar;
    private Button btnRecusar;
    private DatabaseReference referenciaUsuarios;
    private RelativeLayout card;
    ScrollView scrollView;
    ArrayList<Usuario> arrayUsuarios;
    ListView listViewUsuarios;
    UsuariosAdapter adapter;
    ImageView btnVoltar;
    int contaClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        contaClick = 0;

        btnAprovar = findViewById(R.id.btn_aprovar);
        btnRecusar = findViewById(R.id.btn_recusar);

        btnVoltar = findViewById(R.id.botao_voltar);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        card = findViewById(R.id.card_user);

        nome = findViewById(R.id.nome_usuario);
        email = findViewById(R.id.email_usuario);
        telefone = findViewById(R.id.telefone_usuario);

        carImagem = findViewById(R.id.car_image);
        docImagem = findViewById(R.id.doc_imagem);

        card.setVisibility(View.INVISIBLE);
        btnAprovar.setVisibility(View.INVISIBLE);
        btnRecusar.setVisibility(View.INVISIBLE);

        scrollView = findViewById(R.id.scroll_view);
        titulo_navbar = findViewById(R.id.titulo_navbar);

        buscarInformacoes();

        btnRecusar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(getIntent());
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();

                if(scrollY > 650) {
                    titulo_navbar.setText("Usuários");
                } else {
                    titulo_navbar.setText("Aprovar Motoristas");
                }
            }
        });

        arrayUsuarios = new ArrayList<Usuario>();
        buscarUsuarios();

        listViewUsuarios = (ListView) findViewById(R.id.lista_usuarios);
        listViewUsuarios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout detalhes = (RelativeLayout) view.findViewById(R.id.detalhes_corrida);

                if(contaClick == 0) {
                    contaClick++;
                    detalhes.setVisibility(View.VISIBLE);
                } else {
                    contaClick = 0;
                    detalhes.setVisibility(View.GONE);
                }
            }
        });

        SearchView simpleSearchView = (SearchView) findViewById(R.id.simpleSearchView); // inititate a search view
        simpleSearchView.setOnQueryTextListener(this);


    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        listViewUsuarios.setFilterText(text);
        return false;
    }

    public void buscarUsuarios() {
        referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        referenciaUsuarios.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()){
                        Usuario usuario = each.getValue(Usuario.class);
                        arrayUsuarios.add(usuario);
                        adapter = new UsuariosAdapter(HomeActivity.this, arrayUsuarios);
                    }

                    listViewUsuarios.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void buscarInformacoes() {
        referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        referenciaUsuarios.orderByChild("tipo").equalTo("M").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()){
                        final Usuario usuario = each.getValue(Usuario.class);

                        if (!usuario.getAprovado()) {
                            nome.setText(usuario.getNomeCompleto());
                            email.setText(usuario.getEmail());

                            if(usuario.getTelefone() != null) {
                                telefone.setText("Indefinido");
                            } else {
                                telefone.setText(usuario.getTelefone());
                            }

                            // Glide.with(HomeActivity.this).load(usuario.getCarro().getImgCarro()).into(carImagem);
                            // Glide.with(HomeActivity.this).load(usuario.getCarro().getImgDocumento()).into(docImagem);

                            btnAprovar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    referenciaUsuarios.child(usuario.getId()).child("aprovado").setValue(true);
                                    finish();
                                    startActivity(getIntent());
                                }
                            });

                            card.setVisibility(View.VISIBLE);
                            btnAprovar.setVisibility(View.VISIBLE);
                            btnRecusar.setVisibility(View.VISIBLE);
                        } else {
                            card.setVisibility(View.INVISIBLE);
                            btnAprovar.setVisibility(View.INVISIBLE);
                            btnRecusar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
