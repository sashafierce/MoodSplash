package com.sashafierce.moodsplash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.GregorianCalendar;

public class AddMoodActivity extends Activity implements OnClickListener {

    private Button addTodoBtn;
    private EditText subjectEditText;

    private Target target;
    Bitmap result;
    String appendUrl;
    String url;
    StringBuilder sb;
    String baseUrl = "https://source.unsplash.com/600x750/?";
    ProgressDialog progressDialog;
    private DBManager dbManager;
    Cursor cursor;
    SetWallpaper setWallpaperTask;
   // AlarmReceiver ar;
    private DatabaseHelper databaseHelper;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Record");

        setContentView(R.layout.activity_add_mood);

        subjectEditText = (EditText) findViewById(R.id.subject_edittext);

        sb = new StringBuilder();
        addTodoBtn = (Button) findViewById(R.id.add_record);

        dbManager = new DBManager(this);
        databaseHelper = new DatabaseHelper(this);
        dbManager.open();
        addTodoBtn.setOnClickListener(this);

        setWallpaperTask = new SetWallpaper(getApplicationContext() , getBaseContext() , this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_record:

                final String name = subjectEditText.getText().toString();

                dbManager.insert(name);
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                String query =  "SELECT * FROM MOODS ORDER BY RANDOM() LIMIT 1";
                Log.d("Query " ,query);
                cursor = db.rawQuery(query,null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    sb = new StringBuilder();
                    sb.append("");
                    if(cursor.moveToFirst())
                        sb.append(cursor.getString(cursor.getColumnIndex("subject")) );

                    appendUrl = sb.toString();
                    url = baseUrl + appendUrl;
                }
                else url = "https://source.unsplash.com/random";
                cursor.close();

                Toast.makeText(getApplicationContext(), url , Toast.LENGTH_LONG).show();

                if(isOnline()){

                    setWallpaperTask.url = url;
                    setWallpaperTask.execute();
                } else{
                    Toast.makeText(getApplicationContext(),"Error Connecting to server",Toast.LENGTH_LONG).show();

                }
                Intent main = new Intent(AddMoodActivity.this, MoodListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(main);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();


        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "addMood Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.sashafierce.moodsplash/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();


        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "addMood Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.sashafierce.moodsplash/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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