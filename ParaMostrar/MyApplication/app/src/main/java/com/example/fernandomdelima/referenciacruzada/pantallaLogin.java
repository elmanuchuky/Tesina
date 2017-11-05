package com.example.fernandomdelima.referenciacruzada;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class pantallaLogin extends AppCompatActivity {

    public Button cmdLogin;
    private EditText emailField, passwordField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_login);
        init();
    }

    private void init() {
        emailField = (EditText) findViewById(R.id.txtMail);
        passwordField = (EditText) findViewById(R.id.txtPassword);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };

        cmdLogin = (Button)findViewById(R.id.cmdLogin);
        cmdLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(emailField.getText().toString(), passwordField.getText().toString());
            }
        });
    }

    public boolean isLogged(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    private void signIn(final String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("users/" + email.replace(".", ",") + "/lastSeen/", new Timestamp(System.currentTimeMillis()));
                        myRef.updateChildren(childUpdates);
                        user = mAuth.getCurrentUser();
                        Class pantallaPrincipalClass = pantallaPrincipal.class;
                        Intent intent = new Intent(pantallaLogin.this, pantallaPrincipalClass);
                        startActivity(intent);
                    } else {
                        Toast.makeText(pantallaLogin.this, "Authentication failed." + task.getException().toString(), Toast.LENGTH_SHORT).show();
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
}
