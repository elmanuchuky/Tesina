package com.example.fernandomdelima.referenciacruzada;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Handler;

/**
 * Created by Fernando M. de Lima on 10/26/2017.
 */

public class DataBaseManager {
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseUser user;

    public DataBaseManager(){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

    }

    public boolean hostAvailable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (IOException e) {
            // Either we have a timeout or unreachable host or failed DNS lookup
            System.out.println(e);
            return false;
        }
    }
    public boolean hasActiveInternetConnection() {
        boolean success = false;
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            success = (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
        } catch (IOException e) {
        }
        return success;
    }

    /*
    * Devuelve un String interpretando los datos del archivo "file"
    * @InputStream is = lectura de cadena de bytes
    * */
    public static String stringFromStream(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null)
                sb.append(line).append("\n");
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    * Devuelve un ArrayList<String> interpretando los datos del archivo "file"
    * @InputStream is = lectura de cadena de bytes
    * */
    public static ArrayList<String> arrayStringFromStream(InputStream is) {
        try {
            ArrayList<String> list = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while ((line = reader.readLine()) != null){
                list.add(line);
            }
            //sb.append(line).append("\n");
            reader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    * Devuelve una cadena String con la informacion dentro de "file"
    * @Context contex = recibe this como parametro de entrada
    * @String file = ruta mas nombre con extension del archivo a leer
    * */
    public String readFromFile(Context context, String file) {
        String str = "";
        try {
            FileInputStream fis = context.openFileInput(file);
            str = stringFromStream(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /*
    * Devuelve un ArrayList<String> con la informacion dentro de "file"
    * @Context contex = recibe this como parametro de entrada
    * @String file = ruta mas nombre con extension del archivo a leer
    * */
    public ArrayList<String> readArrayFromFile(Context context, String file) {
        ArrayList<String> list = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(file);
            list = arrayStringFromStream(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Se pasa como parametro un stringify de JSON y se devuelve el JSONObject para su uso
    public JSONObject getJSONFromString(Context context, String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            return json;
        } catch (Throwable t) {
            Toast.makeText(context, "Error en la carga de datos " + t.toString(), Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /*
    * @Context contex = recibe this como parametro de entrada
    * @String file = ruta mas nombre con extension del archivo a escribir en agregado
    * @JSONObject data = recibe el valor a ser escrito en agregado
    * */
    public void writeToFile(Context context, String file, String content) {
        FileOutputStream outputStream;
        try {
            content = "\n" + content;
            outputStream = context.openFileOutput(file, Context.MODE_APPEND);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * @String file = ruta mas nombre con extension del archivo a borrar
    * */
    public void eraseFile(Context context, String file) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(file, Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cargo los criterios en mi criteriaList
    public ArrayList<Criteria> loadCriteriaList(Context context){
        JSONObject json = getJSONFromString(context, readFromFile(context, "DB.json"));
        ArrayList<Criteria> criteriaList = new ArrayList<>();
        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                Criteria criteria = new Criteria();
                criteria.setKey(key);
                Object value = json.get(key);
                JSONObject jsonCriteria = getJSONFromString(context, json.get(key).toString());
                Iterator<String> innerIter = jsonCriteria.keys();
                if (innerIter.hasNext()){
                    String key2 = innerIter.next();
                    criteria.setDescription(jsonCriteria.get(key2).toString());
                    if (innerIter.hasNext()){
                        String options = innerIter.next();
                        Object val = jsonCriteria.get(options);
                        criteria.setOptionList(loadOptions(context, val.toString()));
                    }
                }
                criteriaList.add(criteria);
            } catch (JSONException e) {
                Log.d(e.getStackTrace().toString(), e.getMessage());
            }
        }
        return criteriaList;
    }

    // Cargo cada arreglo de mis options
    private ArrayList<Option> loadOptions(Context context, String val) {
        JSONObject jsonOptions = getJSONFromString(context, val);
        ArrayList<Option> options = new ArrayList<>();
        Iterator<String> iter = jsonOptions.keys();
        while (iter.hasNext()){
            String key = iter.next();
            try {
                Option option = new Option();
                option.setKey(key);
                Object value = jsonOptions.get(key);
                JSONObject jsonOption = getJSONFromString(context, jsonOptions.get(key).toString());
                Iterator<String> innerIter = jsonOption.keys();
                if (innerIter.hasNext()){
                    String key2 = innerIter.next();
                    option.setDescription(jsonOption.get(key2).toString());
                    if (innerIter.hasNext()){
                        String op = innerIter.next();
                        Object va = jsonOption.get(op);
                        option.setEntities(loadEntities(context, va.toString()));
                    }
                }
                options.add(option);
            } catch (JSONException e){
            }
        }
        return options;
    }

    // Cargo cada arreglo de entidades en mis entities
    private ArrayList<String> loadEntities(Context context, String s) {
        JSONObject jsonSpecies = getJSONFromString(context, s);
        ArrayList<String> species = new ArrayList<>();
        Iterator<String> iter = jsonSpecies.keys();
        while (iter.hasNext()){
            String key = iter.next();
            if (!Objects.equals(key, "value")){
                species.add(key);
            }
        }
        return species;
    }

    // Devuelve como boolean si se encuentra logueado alguna cuenta de FB
    public boolean isLogged(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    // Recibe como parametro un DataSnapshot recursivamente incrementando por nivel el n para luego finalizar devolviendo un JSONStringify
    public String getInsideContent(DataSnapshot data, int n){
        String s = "";
        s += "\"" + data.getKey() + "\"";
        ++n;
        if (data.hasChildren()) {
            if (n == 1)
                s += "{";
            else
                s += ":{";
            boolean isFirstStep = true;
            for (DataSnapshot k : data.getChildren()) {
                if (isFirstStep)
                    isFirstStep = false;
                else
                    s += ",";
                s += getInsideContent(k, n);
            }
            s += "}";
            return s;
        }else{
            if (data.getValue() instanceof String)
                return s + " : \"" + data.getValue().toString() + "\"";
            else
                return s + " : " + data.getValue();
        }
    }

    // Trae desde la DB de FB la informacion referenciada a los criterios y actualiza la DBJSON interna
    public void updateDBFile(final Context context, final String file){
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("criteria");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                eraseFile(context, file);
                String myJSON = getInsideContent(snapshot, 0);
                writeToFile(context, file, myJSON.substring(myJSON.indexOf("{")));
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error en la carga de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    // Actualiza la DB en FB segun la data que se le pase de forma string en formato JSON y se almacena en el path indicado de FB
    public void updateDB(String path, String data, Context context) {
        JSONObject json = getJSONFromString(context, data);
        try {
            myRef.child(path).updateChildren(jsonToMap(json));
        }catch (Exception ex){
            Log.d("updateDB ERROR", ex.toString());
        }
    }

    public boolean onNewEncounterAdded(final String specie, final int amount) {
        final boolean[] response = new boolean[1];
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("entities/" + specie + "/found");
        /*
        myRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                int value = 0;
                Log.d("ES", "" + mutableData.getValue().toString());

                if(mutableData.getValue() != null) {
                    String found = (String) mutableData.getValue();
                    value = Integer.parseInt(found);
                }
                ++value;
                mutableData.setValue(value);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("Error on complete", "postTransaction:onComplete:" + databaseError);
            }
        });
        */

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try{
                    int found = (Integer.parseInt(snapshot.getValue().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("found", found + amount);
                    myRef = database.getReference("entities/" + specie);
                    myRef.updateChildren(childUpdates);
                } catch (Exception ex){
                    Log.d("onNewEncounterAdded", ex.toString());
                }
                response[0] = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return response[0];
    }

    public void synchronizeFiles(Context context) throws JSONException {
        myRef = database.getReference("");
        ArrayList<String> encounters = new ArrayList<>();
        encounters = readArrayFromFile(context, "newEncounters.json");
        HashMap list = new HashMap();
        for (String encounter : encounters){
            JSONObject job = getJSONFromString(context, encounter);
            String enc = job.getString("specie");
            if (list.containsKey(enc)){
                int count = (int)(list.get(enc)) + 1;
                list.remove(enc);
                list.put(enc, count);
            }else{
                list.put(enc, 1);
            }
            myRef = database.getReference("");
            String key = myRef.push().getKey();
            updateDB("encounters/" + key, encounter, context);
            JSONObject jo = getJSONFromString(context, encounter);
            Map<String, Object> childUpdates = new HashMap<>();
            if (isLogged())
                childUpdates.put("users/" + user.getEmail().replace(".", ",") + "/species/" + key, true);
            else
                childUpdates.put("users/guest/species/" + key, true);
            if (!Objects.equals(jo.getString("groupId"), "idgroup")){ // cambiar la condicion a que tenga sentido en la ejecucion
                childUpdates.put("groups/" + jo.getString("groupId") + "/species/" + key, true);
            }
            if (!Objects.equals(jo.getString("timeZone"), "asd")){ // cambiar la condicion a que tenga sentido en la ejecucion
                childUpdates.put("timeZone/" + jo.getString("timeZone") + "/species/" + key, true);
            }
            if (!Objects.equals(jo.getString("cityName"), "asd")){ // cambiar la condicion a que tenga sentido en la ejecucion
                childUpdates.put("cityName/" + jo.getString("cityName") + "/species/" + key, true);
            }
            myRef.updateChildren(childUpdates);
        }
        for (Object o : list.keySet()) {
            String ob = (String) o;
            onNewEncounterAdded(ob, (int) list.get(o));
        }
        eraseFile(context, "newEncounters.json");
        updateDBFile(context, "DB.json");
    }
}
