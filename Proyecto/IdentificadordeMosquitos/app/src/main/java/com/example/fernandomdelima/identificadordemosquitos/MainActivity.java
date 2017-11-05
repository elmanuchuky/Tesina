package com.example.fernandomdelima.identificadordemosquitos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;

import android.support.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";

    public Button cmdLogin, cmdRegister, cmdLogout;
    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    public void init(){
        cmdLogin = (Button)findViewById(R.id.cmdLogin);
        cmdRegister = (Button)findViewById(R.id.cmdRegister);
        cmdLogout = (Button)findViewById(R.id.cmdLogout);
        cmdLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(emailField.getText().toString(), passwordField.getText().toString());/*
                if (isLogged()){
                    Class plop = MainPage1.class;
                    Intent intent = new Intent(MainActivity.this, plop);
                    startActivity(intent);
                }*/
            }
        });
        cmdRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(emailField.getText().toString(), passwordField.getText().toString());
            }
        });
        cmdLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    public boolean isLogged(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed." + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = mAuth.getCurrentUser();
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed." + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailField = (EditText) findViewById(R.id.txtMail);
        passwordField = (EditText) findViewById(R.id.txtPassword);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };
        init();
        Button btn = (Button) findViewById(R.id.cmdRead);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("");
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String texto = getInsideContent(dataSnapshot, 0);
                        ((TextView)findViewById(R.id.txtTest)).setMovementMethod(new ScrollingMovementMethod());
                        ((TextView)findViewById(R.id.txtTest)).setText(texto);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        TextView myText = (TextView)findViewById(R.id.txtTest);
                        myText.setText("Error");
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override

    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public String getInsideContent(DataSnapshot data, int n){
        String s = "";
        if (n == 0){
            if (isLogged())
                s += user.getEmail().toString();
            else
                s += "Visitante";
        }
        s += "\n" + data.getKey();
        String c = "";
        ++n;
        for (int i = 0; i < n; i++)
            c += "    ";
        if (data.hasChildren()) {
            for (DataSnapshot k : data.getChildren()) {
                s += "\n" + c;
                s += getInsideContent(k, n);
            }
            return s;
        }else{
            return s + " : " + data.getValue().toString();
        }
    }
}
