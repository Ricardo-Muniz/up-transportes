package com.upbrasil.up.helper;

import android.app.Activity;

import com.kaopiz.kprogresshud.KProgressHUD;

public class Loading {
    private Activity activity;
    KProgressHUD progress;

    public Loading(Activity activity) {
        this.activity = activity;

        progress = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    public void mostrarLoading() {
        progress.show();
    }

    public void fecharLoading() {
        progress.dismiss();
    }
}
