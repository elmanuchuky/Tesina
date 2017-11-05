package com.example.fernandomdelima.referenciacruzada;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class pantallaIdentificacion extends AppCompatActivity {

    Button btnNext, btnFinish;
    private EditText groupIdField;
    ArrayList<String> speciesLeft, criteriaLeft;
    ArrayList<Criteria> criteriaList;
    int currentCriteria, firstCriteria;
    String groupId;
    boolean hasStarted, isFirst;
    ArrayList<VWCriteriaSelection> criteriaSelectionList;
    // Para la obtencion de coordenadas
    String latitude, longitude, cityName;
    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_identificacion);
        init();
    }

    private void init() {
        getCurrentLoaction();
        ((TextView) findViewById(R.id.txtCriteria)).setMovementMethod(new ScrollingMovementMethod());
        groupIdField = (EditText) findViewById(R.id.txtGroupId);
        currentCriteria = -1;
        hasStarted = false;
        isFirst = true;
        groupId = "";
        speciesLeft = new ArrayList<String>();
        criteriaLeft = new ArrayList<String>();
        criteriaList = new ArrayList<Criteria>();
        criteriaSelectionList = new ArrayList<VWCriteriaSelection>();
        loadDataFromDBJSON();
        setClickEventToNextButton();
        setClickEventToFinishButton();
    }

    private void setClickEventToFinishButton() {
        btnFinish = (Button) findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasStarted) {
                    RadioGroup rgpOptions = (RadioGroup) findViewById(R.id.rgpOptions);
                    int optionselected = rgpOptions.getCheckedRadioButtonId();
                    if (optionselected != criteriaList.get(currentCriteria).getOptionList().size())
                        addOptionSelected(optionselected);
                }
                groupId = groupIdField.getText().toString();
                Encounter encounter = new Encounter();
                encounter.setLat(latitude);
                encounter.setLon(longitude);
                encounter.setCityName(cityName);
                encounter.setGroupId("idgroupa");
                encounter.setSpecie("gen-esp"); // Falta mejorar el filtro y sacarle la harcodeada
                DataBaseManager DBM = new DataBaseManager();
                DBM.writeToFile(pantallaIdentificacion.this, "newEncounters.json", encounter.toString());
            }
        });
    }

    private void setClickEventToNextButton() {
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasStarted) {
                    RadioGroup rgpOptions = (RadioGroup) findViewById(R.id.rgpOptions);
                    int optionselected = rgpOptions.getCheckedRadioButtonId();
                    if (optionselected != criteriaList.get(currentCriteria).getOptionList().size()) {
                        addOptionSelected(optionselected);
                        if (isFirst) {
                            loadSpeciesInFirstAnswer(firstCriteria, optionselected);
                            isFirst = false;
                        } else {
                            removeSpeciesUnmatching(optionselected);
                        }
                    }
                }
                hasStarted = true;
                if (criteriaLeft.size() > 0) {
                    checkCriteria();
                    getNextCriteria();
                    if (firstCriteria != -1)
                        firstCriteria = currentCriteria;
                    drawOptions();
                    if (criteriaLeft.size() == 0)
                        setEndingButtons();
                }
            }
        });
    }

    // Selecciona el siguiente criterio en donde tenga sentido seguir preguntando
    private void checkCriteria() {
        getNextCriteria();
        if (getReferences() < 1) { // Recordar de ver que se hace con la pregunta que se ignora!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            if (firstCriteria != -1)
                firstCriteria = currentCriteria;
            drawOptions();
            if (criteriaLeft.size() == 0)
                setEndingButtons();
            return;
        } else {
            checkCriteria();
            return;
        }
    }

    // Comprueba la cantidad de opciones que tienen referencia con la lista restante de entidades
    private int getReferences() {
        int quantity = 0;
        for (Option o : criteriaList.get(currentCriteria).getOptionList()) {
            boolean wasFound = false;
            for (String s : speciesLeft)
                if (o.getEntities().contains(s))
                    wasFound = true;
            if (wasFound)
                ++quantity;
        }
        return quantity;
    }

    // Quita de la lista de entidades restantes, las que no esten contenidas dentro del criterio actual
    private void removeSpeciesUnmatching(int optionselected) {
        for (String s : speciesLeft) {
            if (!(criteriaList.get(currentCriteria).getOptionList().get(optionselected).getEntities().contains(s)))
                speciesLeft.remove(s);
        }
    }

    // Trae a la lista de entidades restantes todas las relacionadas con la primer pregunta
    private void loadSpeciesInFirstAnswer(int firstCriteria, int optionselected) {
        ArrayList<String> list = criteriaList.get(firstCriteria).getOptionList().get(optionselected).getEntities();
        for (String s : list) {
            speciesLeft.add(s);
        }
    }

    // Trae desde la DB JSON interna la informacion referenciada a los criterios
    private void loadDataFromDBJSON() {
        loadCriteriaList();
        loadCriteriaLeft();
        Button btnNextEnable = (Button) findViewById(R.id.btnNext);
        btnNextEnable.setEnabled(true);
    }

    //
    private void addOptionSelected(int optionselected) {
        VWCriteriaSelection selection = new VWCriteriaSelection();
        selection.setCriteriaKey(criteriaList.get(currentCriteria).getKey());
        selection.setOptionKey(criteriaList.get(currentCriteria).getOptionList().get(optionselected).getKey());
        criteriaSelectionList.add(selection);
    }

    // Dibuja en pantalla de forma dinamica todas las posibles respuestas
    private void drawOptions() {
        RadioGroup rgpOptions = (RadioGroup) findViewById(R.id.rgpOptions);
        rgpOptions.removeAllViews();
        int lastId = 0;
        for (int i = 0; i < criteriaList.get(currentCriteria).getOptionList().size(); i++) {
            RadioButton rdbtn = new RadioButton(this);
            lastId = i;
            rdbtn.setId(i);
            rdbtn.setText(criteriaList.get(currentCriteria).getOptionList().get(i).getDescription());
            rdbtn.setAllCaps(true);
            rdbtn.setTextSize(18);
            rgpOptions.addView(rdbtn);
        }
        RadioButton rdbtn = new RadioButton(this);
        rdbtn.setId(lastId + 1);
        rdbtn.setText("No lo se");
        rdbtn.setAllCaps(true);
        rdbtn.setTextSize(18);
        rgpOptions.addView(rdbtn);
        rgpOptions.check(rdbtn.getId());
    }

    // Configura la visual de la botonera
    private void setEndingButtons() {
        Button btnFinishEnable = (Button) findViewById(R.id.btnFinish);
        btnFinishEnable.setEnabled(true);
        Button btnNextEnable = (Button) findViewById(R.id.btnNext);
        btnNextEnable.setEnabled(false);
    }

    // Cargo los criterios en mi criteriaList
    private void loadCriteriaList() {
        DataBaseManager DBM = new DataBaseManager();
        criteriaList = DBM.loadCriteriaList(pantallaIdentificacion.this);
    }

    // Cargo el arreglo criteriaLeft con las claves de cada criterio existente
    private void loadCriteriaLeft() {
        for (Criteria criteria : criteriaList) {
            criteriaLeft.add(criteria.getKey());
        }
    }

    // Trae la siguiente pregunta de la lista de la DB en un orden de mayor cantidad de
    public void getNextCriteria() {
        int mayor = -1;
        int pos = -1;
        Criteria criteria = new Criteria();
        for (Criteria c : criteriaList) {
            if (c.getOptionList().size() > mayor && criteriaLeft.contains(c.getKey())) {
                mayor = c.getOptionList().size();
                pos = criteriaList.indexOf(c);
                currentCriteria = pos;
                criteria = c;
            }
        }
        try {
            criteriaLeft.remove(criteriaLeft.indexOf(criteria.getKey()));
        } catch (Exception e) {
        }
        try {
            ((TextView) findViewById(R.id.txtCriteria)).setText(criteriaList.get(pos).getDescription());
        } catch (Exception e) {
        }
    }

    // Obtiene las coordenadas cada cierto tiempo
    public void getCurrentLoaction() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = "" + location.getLatitude();
                TextView txtLatitude = (TextView) findViewById(R.id.txtLatitude);
                txtLatitude.setText("Latitud: " + latitude);
                longitude = "" + location.getLongitude();
                TextView txtLongitude = (TextView) findViewById(R.id.txtLongitude);
                txtLongitude.setText("Longitud: " + longitude);

                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        cityName = addresses.get(0).getLocality();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(pantallaIdentificacion.this, "Por favor, habilite el GPS para continuar", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("network", 1000, 0, locationListener);
    }
}
