package com.example.plplbproject.controleur;

import android.view.View;
import android.widget.TextView;

import com.example.plplbproject.Vue.LoginActivity;

import metier.LoginModele;

public class LoginClickListener implements View.OnClickListener {

    LoginActivity vue;
    LoginModele modele;

    public LoginClickListener(LoginModele modele, LoginActivity vue){
        this.vue = vue;
        this.modele = modele;
    }

    @Override
    public void onClick(View view) {

        if(modele.acceptINE(vue.getLoginInput())){
            vue.switchIntent();
        }else{
            vue.setTextError("INE invalide (format : ab123456)");
            vue.clearLoginInput();
        }
    }
}
