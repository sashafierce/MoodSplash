package com.sashafierce.moodsplash;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;
import android.os.Handler;

public class Setting extends Activity implements
        OnItemSelectedListener {
    Spinner s1, s2;
    String url;
    StringBuilder sb;
    String appendUrl;
    String baseUrl = "https://source.unsplash.com/600x750/?";
    Cursor cursor;
    SetWallpaper setWallpaperTask;
    Handler mHandler;

    private DatabaseHelper databaseHelper;
    private DBManager dbManager;
    Runnable myRunnable;
    boolean isRunning;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        s1 = (Spinner) findViewById(R.id.spinner1);
        s2 = (Spinner) findViewById(R.id.spinner2);
        s1.setOnItemSelectedListener(this);
        sb = new StringBuilder();
        dbManager = new DBManager(this);
        dbManager.open();
        isRunning = false;
        databaseHelper = new DatabaseHelper(this);

        setWallpaperTask = new SetWallpaper(getApplicationContext(), getBaseContext(), this);
        Timer timer = new Timer();
        mHandler = new Handler();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        // TODO Auto-generated method stub
        String sp1 = String.valueOf(s1.getSelectedItem());

        if (sp1.contentEquals("Day(s)")) {
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 1; i <= 6; i++) {
                list.add(i);
            }
            ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            s2.setAdapter(dataAdapter);
        }

        if (sp1.contentEquals("Week(s)")) {
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 1; i <= 2; i++) {
                list.add(i);
            }
            ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            s2.setAdapter(dataAdapter);
        }
        if (sp1.contentEquals("Hour(s)")) {
            List<Integer> list = new ArrayList<Integer>();
            for (int i = 1; i <= 12; i++) {
                list.add(i);
            }
            ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            s2.setAdapter(dataAdapter);
        }
        if (sp1.contentEquals("None")) {
            List<Integer> list = new ArrayList<Integer>();

            ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            dataAdapter.notifyDataSetChanged();
            s2.setAdapter(dataAdapter);
            isRunning = false;

        } else {
            Toast.makeText(this, "Timer triggered!", Toast.LENGTH_LONG).show();
        }
        if (isRunning) {
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (isRunning) {
                                try {
                                    // Thread.sleep(10000);
                                    mHandler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            SQLiteDatabase db = databaseHelper.getReadableDatabase();
                                            String query = "SELECT * FROM MOODS ORDER BY RANDOM() LIMIT 1";
                                            Log.d("Query ", query);
                                            Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
                                            cursor = db.rawQuery(query, null);

                                            if (cursor != null) {
                                                cursor.moveToFirst();
                                                sb = new StringBuilder();
                                                sb.append("");
                                                if (cursor.moveToFirst())
                                                    sb.append(cursor.getString(cursor.getColumnIndex("subject")));
                                                appendUrl = sb.toString();
                                                url = baseUrl + appendUrl;
                                            } else url = "https://source.unsplash.com/random";
                                            cursor.close();
                                            if (isOnline()) {
                                                setWallpaperTask.url = url;
                                                setWallpaperTask.execute();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error Connecting to server", Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                            }


                        }
                    }).start();

                }
            }, 0, 10000);

        }
    }




    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
}
