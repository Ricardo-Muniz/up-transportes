package com.upbrasil.up.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.upbrasil.up.R;

public class CartaoCreditoActivity extends AppCompatActivity {
    ImageView botaoVoltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao_credito);

        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void mostrarConfirmacao(String mensagem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(CartaoCreditoActivity.this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setMessage(mensagem)
                .setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.show();
    }
}
