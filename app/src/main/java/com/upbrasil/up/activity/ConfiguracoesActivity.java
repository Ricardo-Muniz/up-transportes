package com.upbrasil.up.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.upbrasil.up.R;
import com.upbrasil.up.config.ConfiguracaoFirebase;
import com.upbrasil.up.helper.Alerts;
import com.upbrasil.up.helper.Loading;
import com.upbrasil.up.helper.Permissions;
import com.upbrasil.up.model.Usuario;

public class ConfiguracoesActivity extends AppCompatActivity {
    ImageView botaoVoltar, profile;
    private DatabaseReference referenciaUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
    Button botaoAlterarTelefone;
    Button botaoAlterarEmail;
    Button botaoAlterarSenha;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    TextView nome, email, telefone;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Usuario usuario;

    private Loading loading;
    private Alerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao();
        user = firebaseAuth.getCurrentUser();

        loading = new Loading(this);
        alerts = new Alerts(this);

        nome = findViewById(R.id.nome_usuario);
        email = findViewById(R.id.email_usuario);
        telefone = findViewById(R.id.usuario_telefone);
        profile = findViewById(R.id.foto_profile);


        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button botaoAlterarPerfil = findViewById(R.id.alterar_perfil);
        botaoAlterarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Permissions.Check_CAMERA(ConfiguracoesActivity.this)) {
                    selecionarImagem(2);
                } else {
                    Permissions.Request_CAMERA(ConfiguracoesActivity.this, 22);
                }
            }
        });

        botaoAlterarEmail = findViewById(R.id.alterar_email);
        botaoAlterarEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracoesActivity.this, AlterarEmailActivity.class);
                startActivity(intent);
            }
        });

        botaoAlterarSenha = findViewById(R.id.alterar_senha);
        botaoAlterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracoesActivity.this, AlterarSenhaActivity.class);
                startActivity(intent);
            }
        });

        botaoAlterarTelefone = findViewById(R.id.alterar_telefone);
        botaoAlterarTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfiguracoesActivity.this, AlterarTelefoneActivity.class);
                startActivity(intent);
            }
        });


        buscarUsuario();
        buscarFotoPerfil();
    }

    private void buscarUsuario() {
        referenciaUsuarios.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot issue : dataSnapshot.getChildren()){
                        usuario = issue.getValue(Usuario.class);
                        email.setText(usuario.getEmail());

                        nome.setText(usuario.getNomeCompleto());

                        if(usuario.getTelefone() != null) {
                            telefone.setText(usuario.getTelefone());
                        }

                        if(usuario.getTelefone() == null) {
                            botaoAlterarTelefone.setText("Adicionar Telefone");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void buscarFotoPerfil() {
        referenciaUsuarios.orderByChild("id").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot each : dataSnapshot.getChildren()) {
                        Usuario usuario = each.getValue(Usuario.class);

                        if(usuario.getFotoPerfil() != null) {
                            Glide.with(ConfiguracoesActivity.this).load(usuario.getFotoPerfil()).into(profile);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void selecionarImagem(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, code);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 2) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap fotoSelecionada = extras.getParcelable("data");
                profile.setImageBitmap(fotoSelecionada);

                loading.mostrarLoading();
                Usuario usuario = new Usuario();
                usuario.setId(user.getUid());
                usuario.atualizarFotoPerfil(ConfiguracoesActivity.this, loading, fotoSelecionada);
            }
        }
    }
}
