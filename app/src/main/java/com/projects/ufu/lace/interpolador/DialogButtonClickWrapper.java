package com.projects.ufu.lace.interpolador;

import android.app.AlertDialog;
import android.view.View;

public abstract class DialogButtonClickWrapper implements View.OnClickListener {
    private AlertDialog dialog;

    protected DialogButtonClickWrapper(AlertDialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onClick (View v){
        if(onClicked()){
            dialog.dismiss();
        }
    }
    protected abstract boolean onClicked();
}
