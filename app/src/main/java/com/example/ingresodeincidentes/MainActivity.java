

package com.example.ingresodeincidentes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import java.util.List;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    Spinner spinnerLab;
    EditText inpRut;
    EditText inpNombre;
    EditText inpDesc;
    Button btnEnviar;

    TextView txtEnvLab;
    TextView txtEnvFecha;
    TextView txtEnvRut;
    TextView txtEnvNombre;
    TextView txtEnvDesc;

    //public LocationManager locManager;
    //public LocationListener locListener;
    //public String locDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) //verificamos que exista acelerometro
        {
            //no se puede agregar mas delay que los especificados
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }

        inpRut = (EditText) findViewById(R.id.inpRut);
        inpNombre = (EditText) findViewById(R.id.inpNombre);
        inpDesc = (EditText) findViewById(R.id.inpDesc);
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        spinnerLab = (Spinner) findViewById(R.id.spinnerLab);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.laboratorios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLab.setAdapter(adapter);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Obtiene si los campos son nulos
                if (TextUtils.isEmpty(inpRut.getText()) || TextUtils.isEmpty(inpNombre.getText()) || TextUtils.isEmpty(inpDesc.getText())) {
                    Toast.makeText(MainActivity.this, "No se ha ingresado un valor", Toast.LENGTH_SHORT).show();
                    return;

                } else if (validarRut(String.valueOf(inpRut.getText())) == false) {
                    Toast.makeText(MainActivity.this, "Hay un problema con el rut", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    String seleccion = spinnerLab.getSelectedItem().toString();
                    crearAlerta();
                }
            }
        });
    }

    //Crea el AlertDialog para mostrar lo que se va a enviar
    public void crearAlerta() {

        AlertDialog.Builder dialogAlerta = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_enviar, null);
        dialogAlerta.setView(dialogView)
                .setTitle("Datos a enviar")
                .setCancelable(false)
                .setPositiveButton("Registrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Datos grabados", Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        //Vincula las variables con las encontradas en dialog_enviar
        txtEnvLab = (TextView) dialogView.findViewById(R.id.txtEnvLab);
        txtEnvFecha = (TextView) dialogView.findViewById(R.id.txtEnvFecha);
        txtEnvRut = (TextView) dialogView.findViewById(R.id.txtEnvRut);
        txtEnvNombre = (TextView) dialogView.findViewById(R.id.txtEnvNombre);
        txtEnvDesc = (TextView) dialogView.findViewById(R.id.txtEnvDesc);

        //reescribe los valores en la view dialog_enviar
        txtEnvLab.setText(spinnerLab.getSelectedItem().toString());
        txtEnvRut.setText(String.valueOf(inpRut.getText()));
        txtEnvNombre.setText(String.valueOf(inpNombre.getText()));
        txtEnvDesc.setText(String.valueOf(inpDesc.getText()));

        //obtenerTiempo();
        //if (locDate != null){
        //    txtEnvFecha.setText(locDate);

        //}else {
        //Obtiene fecha
        LocalDateTime datetime = LocalDateTime.now();
        String fechaRegistro = datetime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        txtEnvFecha.setText(String.valueOf(fechaRegistro));
        //}
        dialogAlerta.show();
    }

    //funciona
    public static boolean validarRut(String rut) {

        boolean validacion = false;
        try {
            rut = rut.toUpperCase();

            //obtiene el rut como int removiendo el digito verificador
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }

    //presiona automaticamente el boton cuando se encuentra en vertical, puede ser molesto
    @Override
    public void onSensorChanged(SensorEvent event) {
        btnEnviar = (Button) findViewById(R.id.btnEnviar);
        float y = event.values[SensorManager.DATA_Y];
        //mas exacto para que no se active tan facilmente
        if (y > 9.4){
            //simula un click en el boton para iniciar el grabado
            btnEnviar.performClick();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
    /* No funciona
    public void obtenerTiempo() {
        try {
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            conectaGps(loc);
        } catch (Exception e) {
            Log.d("fecha", e.toString());
            e.getCause();
        }
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                conectaGps(location);
            }
            public void onProviderDisabled(String provider) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locListener); //TIEMPO DE REFRESH PARA GPS. 0.1 SEGUNDO
        } catch (Exception e) {
            e.getCause();
        }
    }
    private void conectaGps(Location loc) {
        try{
            if(loc != null)
            {
                locDate = String.valueOf(loc.getTime());
                //String d = new Date(location.getTime()).toString();
                //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Log.d("fecha", locDate);

            }
        }catch(Exception e){
            e.getCause();
        }//frin try
    }//fin procedimiento
    */


