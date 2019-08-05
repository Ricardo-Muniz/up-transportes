package com.upbrasil.up.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;

import com.crowdfire.cfalertdialog.CFAlertDialog;

public class Alerts {
    private Activity activity;

    public Alerts(Activity activity) {
        this.activity = activity;
    }

    public void mostrarPopupErro(String mensagem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(activity)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setMessage(mensagem)
                .setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.show();
    }

    public void mostrarPopupSucesso(String mensagem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(activity)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setMessage(mensagem)
                .setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                activity.finish();
            }
        }, 2000);
    }

    public void mostrarPopupSucessoSemRedirect(String mensagem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(activity)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setMessage(mensagem)
                .setTextGravity(Gravity.CENTER_HORIZONTAL);
        builder.show();
    }

    public void mostraPopUpGPS(String mensagem) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(activity)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setMessage(mensagem)
                .setCancelable(false)
                .setTextGravity(Gravity.CENTER_HORIZONTAL).addButton("ATIVAR LOCALIZAÇÃO", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, (dialog, which) -> {
                    activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                });

        builder.show();
    }
}
