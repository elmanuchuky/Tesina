package com.example.fernandomdelima.referenciacruzada;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class mainActivity extends AppCompatActivity {

    Button btnLogin;
    Button btnSignup;
    Button btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        init();
    }

    private void init() {
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnSignup = (Button)findViewById(R.id.btnSignup);
        btnGuest = (Button)findViewById(R.id.btnGuest);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class pantallaLoginClass = pantallaLogin.class;
                Intent intent = new Intent(mainActivity.this, pantallaLoginClass);
                startActivity(intent);
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class pantallaSignupClass = pantallaSignup.class;
                Intent intent = new Intent(mainActivity.this, pantallaSignupClass);
                startActivity(intent);
            }
        });
        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class pantallaPrincipalClass = pantallaPrincipal.class;
                Intent intent = new Intent(mainActivity.this, pantallaPrincipalClass);
                startActivity(intent);
            }
        });
    }
}
