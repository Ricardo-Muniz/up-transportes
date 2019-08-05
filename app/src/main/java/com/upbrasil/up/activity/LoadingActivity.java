package com.upbrasil.up.activity;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.upbrasil.up.R;
import com.upbrasil.up.helper.Permissoes;
import com.upbrasil.up.helper.UsuarioFirebase;

public class LoadingActivity extends AppCompatActivity {
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

       if(Permissoes.validarPermissoes(permissoes, this, 1)) {
           UsuarioFirebase.redirecionaUsuarioLogado(LoadingActivity.this);
       }
    }
}
