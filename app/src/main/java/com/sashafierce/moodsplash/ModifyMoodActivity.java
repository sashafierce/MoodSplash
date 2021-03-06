package com.sashafierce.moodsplash;


import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModifyMoodActivity extends Activity implements OnClickListener {

    private EditText titleText;
    private Button updateBtn, deleteBtn;
    Bitmap result;
    String appendUrl;
    String url;
    StringBuilder sb;
    int length , height ;
    String baseUrl = "https://source.unsplash.com/600x750/?";
    ProgressDialog progressDialog;
    String term;
    private List<String> list ;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    Cursor cursor;

    private DatabaseHelper databaseHelper;
    private long _id;
    private DBManager dbManager;
    SetWallpaper setWallpaperTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Modify Record");

        setContentView(R.layout.activity_modify_mood);
        sb = new StringBuilder();
        dbManager = new DBManager(this);
        dbManager.open();
        databaseHelper = new DatabaseHelper(this);

       // titleText = (EditText) findViewById(R.id.subject_edittext);

        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        term = intent.getStringExtra("title");

        _id = Long.parseLong(id);

      //  titleText.setText(name);
        ref =  FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        list = new ArrayList<String>();

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        setWallpaperTask = new SetWallpaper(getApplicationContext() , getBaseContext() , this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
               // String title = titleText.getText().toString();

                url = baseUrl + term;

               // Toast.makeText(getApplicationContext(), url , Toast.LENGTH_LONG).show();
                if(isOnline()){
                    setWallpaperTask.url = url;
                    setWallpaperTask.execute();
                } else{
                    Toast.makeText(getApplicationContext(),"Error Connecting to server",Toast.LENGTH_LONG).show();

                }
                Intent main = new Intent(ModifyMoodActivity.this, MoodListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(main);
                break;

            case R.id.btn_delete:
                dbManager.delete(_id);
                //Firebase data update
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

                this.returnHome();
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), MoodListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
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




