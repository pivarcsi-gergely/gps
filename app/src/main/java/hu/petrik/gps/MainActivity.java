package hu.petrik.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private TextView TVGps;
    private Timer myTimer;
    private double longitude, latitude;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean writePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            TVGps.setText("Nincs helymeghatározás engedélyezve");

            String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, 0);
            return;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myTimer = new Timer();
        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }
        };
    myTimer.schedule(TT, 1000, 5000);
    }

    private void TimerMethod() {
        this.runOnUiThread(TimerTick);
    }

    private final Runnable TimerTick = new Runnable() {
        @Override
        public void run() {
            TVGps.setText("Longitude: " + longitude + "\nLatitude: " + latitude);
            if (writePermission){
                try {
                    Naplozas.kiir(longitude, latitude);
                }
                catch (IOException e){
                    Log.d("Kiírás:", e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };

    public void init() {
        TVGps = findViewById(R.id.TVGps);
        //LManager: felelős a hely lekérdezéséért
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //LListener: LM eseménykezelője
        locationListener = location -> {
          longitude = location.getLongitude();
          latitude = location.getLatitude();
        };

        //Van-e engedélyünk a fájlba íráshoz/olvasáshoz
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            writePermission = false;

            String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }
}