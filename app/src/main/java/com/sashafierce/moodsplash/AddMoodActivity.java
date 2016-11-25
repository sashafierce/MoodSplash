package com.sashafierce.moodsplash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
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
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class AddMoodActivity extends Activity implements OnClickListener {

    Bitmap result;
    String appendUrl;
    String url;
    StringBuilder sb;
    String baseUrl = "https://source.unsplash.com/600x750/?";
    ProgressDialog progressDialog;
    Cursor cursor;
    SetWallpaper setWallpaperTask;
    private Button addTodoBtn;
    private Button speechBtn;
    private EditText subjectEditText;
    private Target target;
    private DBManager dbManager;
    private DatabaseHelper databaseHelper;
    private List<String> list ;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private GoogleApiClient client;
    final static int REQ_CODE = 1;

    private final int RESULT_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Record");

        setContentView(R.layout.activity_add_mood);

        subjectEditText = (EditText) findViewById(R.id.subject_edittext);

        ref =  FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        list = new ArrayList<String>();
        sb = new StringBuilder();
        addTodoBtn = (Button) findViewById(R.id.add_record);
        speechBtn = (Button) findViewById(R.id.btn_speak);

        dbManager = new DBManager(this);
        databaseHelper = new DatabaseHelper(this);
        dbManager.open();
        addTodoBtn.setOnClickListener(this);
        speechBtn.setOnClickListener(this);
        setWallpaperTask = new SetWallpaper(getApplicationContext(), getBaseContext(), this);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_record:

                final String name = subjectEditText.getText().toString();

                dbManager.insert(name);
                url = baseUrl + name;

                // firebase database
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail();
                cursor = dbManager.fetch();

                if (cursor.moveToFirst()){
                    do{
                        String data = cursor.getString(cursor.getColumnIndex("subject"));
                       list.add(data);

                    }while(cursor.moveToNext());
                }
               // User u = new User(username , list);
                cursor.close();
                String username = "";
                 for (int i=0; i<email.length(); i++) {
                    char c = email.charAt(i);

                    if(c == '@') break;
                    if(c == '.' || c == '#' || c == '$' || c == '[' || c == ']' ) ;
                    else username += Character.toString(c);

                }
                ref.child(username).setValue(list);


               // Toast.makeText(getApplicationContext(), url, Toast.LENGTH_LONG).show();

                if (isOnline()) {

                    setWallpaperTask.url = url;
                    setWallpaperTask.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Error Connecting to server", Toast.LENGTH_LONG).show();

                }
                Intent main = new Intent(AddMoodActivity.this, MoodListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(main);
                break;
            case R.id.btn_speak:
                Intent intent = new Intent(this, SpeechToText.class);

                startActivityForResult(intent, REQ_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    String string = data.getStringExtra("term");
                    subjectEditText.setText(string);
                }
                break;
            }

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