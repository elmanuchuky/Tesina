package com.example.fernandomdelima.referenciacruzada;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fernando M. de Lima on 10/23/2017.
 */

public class EncounterManager {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;

    public EncounterManager(){
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
            }
        };
    }

    public void registerNewEncounter(Encounter encounter, String key) {
        //encounter.setUserMail(user.getEmail());
        //String key = myRef.child("encounters/").push().getKey();
        //Map<String, Object> encounterValues = encounter.toMap();
        //Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("encounters/" + key, encounterValues);
        /*
        childUpdates.put("users/" + encounter.getUserMail().replace(".", ",") + "/species/" + key, true);
        if (encounter.getGroupId() != null){
            childUpdates.put("groups/" + encounter.getGroupId() + "/species/" + key, true);
        }
        myRef.updateChildren(childUpdates);
        onNewEncounterAdded(encounter.getSpecie());
        */
    }

    public boolean onNewEncounterAdded(final String specie) {
        final boolean[] response = new boolean[1];
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("entities/" + specie + "/found");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int found = (Integer.parseInt(snapshot.getValue().toString()));
                Map<String, Object> childUpdates = new HashMap<>();
                myRef = database.getReference("entities/" + specie);
                childUpdates.put("found", ++found);
                myRef.updateChildren(childUpdates);
                response[0] = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return response[0];
    }

}
