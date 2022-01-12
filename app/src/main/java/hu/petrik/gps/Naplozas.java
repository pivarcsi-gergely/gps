package hu.petrik.gps;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Naplozas {

    public static void kiir(double longitude, double latitude) throws IOException {
        //Dátum lekérdezése
        Date datum = Calendar.getInstance().getTime();
        //Dátum formázása
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formazottDatum = dateFormat.format(datum);

        //CSV fájl létrehozása
        String sor = String.format("%f, %f, %s", longitude, latitude, formazottDatum);
        //Sub-step: belső tárhely állapotát megvizsgálni;
        String state = Environment.getExternalStorageState();


        if (state.equals(Environment.MEDIA_MOUNTED)){
            //Fájlbaírás: File + Buffered writer
            File file = new File(Environment.getExternalStorageDirectory(), "gps_adatok.csv");
            BufferedWriter br = new BufferedWriter(new FileWriter(file, true));
            br.append(sor);
            br.append(System.lineSeparator()); // /n
            br.close();
        }
    }
}
