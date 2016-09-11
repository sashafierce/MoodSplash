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

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class ModifyMoodActivity extends Activity implements OnClickListener {

    private EditText titleText;
    private Button updateBtn, deleteBtn;
    Bitmap result;
    String appendUrl;
    String url;
    StringBuilder sb;
    String baseUrl = "https://source.unsplash.com/600x750/?";
    ProgressDialog progressDialog;

    Cursor cursor;

    private DatabaseHelper databaseHelper;
    private long _id;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Modify Record");

        setContentView(R.layout.activity_modify_mood);
        sb = new StringBuilder();
        dbManager = new DBManager(this);
        dbManager.open();

        databaseHelper = new DatabaseHelper(this);

        titleText = (EditText) findViewById(R.id.subject_edittext);

        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("title");


        _id = Long.parseLong(id);

        titleText.setText(name);

        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                String title = titleText.getText().toString();

                dbManager.update(_id, title);
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                String query =  "SELECT * FROM MOODS ORDER BY RANDOM() LIMIT 1";
                Log.d("Query " ,query);
                // Toast.makeText(getApplicationContext(), query , Toast.LENGTH_LONG).show();
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
                    new SetWallpaperTask().execute();
                } else{
                    Toast.makeText(getApplicationContext(),"Error Connecting to server",Toast.LENGTH_LONG).show();

                }
                Intent main = new Intent(ModifyMoodActivity.this, MoodListActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                startActivity(main);
                break;

            case R.id.btn_delete:
                dbManager.delete(_id);
                this.returnHome();
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(), MoodListActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }
    public class SetWallpaperTask extends AsyncTask <String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap result= null;

            try {
                result = Picasso.with(getApplicationContext())
                        .load(url)
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute (Bitmap result) {
            super.onPostExecute(result);

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getBaseContext());
            try {
                wallpaperManager.setBitmap(result);

                Toast.makeText(getApplicationContext(), "Set wallpaper successfully", Toast.LENGTH_LONG).show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();

            progressDialog = new ProgressDialog(ModifyMoodActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
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




