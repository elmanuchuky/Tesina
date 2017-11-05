package com.example.fernandomdelima.referenciacruzada;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.TimeZone;

public class pantallaPrincipal extends AppCompatActivity {
    Button btnIdentify, btnSinchronize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        init();
    }

    private void init() {
        String file = "newEncounter.json";
        final String content = "";
        final DataBaseManager DBM = new DataBaseManager();
        DBM.writeToFile(this, content, file);
        btnIdentify = (Button) findViewById(R.id.btnIdentify);
        btnIdentify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class pantallaIdentificacionClass = pantallaIdentificacion.class;
                Intent intent = new Intent(pantallaPrincipal.this, pantallaIdentificacionClass);
                startActivity(intent);
            }
        });
        btnSinchronize = (Button) findViewById(R.id.btnSinchronize);
        btnSinchronize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Toast.makeText(pantallaPrincipal.this, "nopasa nada", Toast.LENGTH_LONG).show();
                if (DBM.hostAvailable("www.google.com", 80)){
                    Toast.makeText(pantallaPrincipal.this, "Hay inter", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(pantallaPrincipal.this, "No hay inter", Toast.LENGTH_LONG).show();
                }*/
                DataBaseManager DBM = new DataBaseManager();
                try {
                    DBM.synchronizeFiles(pantallaPrincipal.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
