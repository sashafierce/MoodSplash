package com.sashafierce.moodsplash;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

public class AddMoodActivity extends Activity implements OnClickListener {

    private Button addTodoBtn;
    private EditText subjectEditText;
    private EditText descEditText;
    private Target target;
    Bitmap result;
    String appendUrl;
    String url;
    StringBuilder sb;
    String baseUrl = "https://source.unsplash.com/600x750/?";
    ProgressDialog progressDialog;
    private DBManager dbManager;
    Cursor cursor;

    private DatabaseHelper databaseHelper;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Add Record");

        setContentView(R.layout.activity_add_record);

        subjectEditText = (EditText) findViewById(R.id.subject_edittext);
        descEditText = (EditText) findViewById(R.id.description_edittext);
        sb = new StringBuilder();
        addTodoBtn = (Button) findViewById(R.id.add_record);

        dbManager = new DBManager(this);
        databaseHelper = new DatabaseHelper(this);
        dbManager.open();
        addTodoBtn.setOnClickListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_record:

                final String name = subjectEditText.getText().toString();
                final String desc = descEditText.getText().toString();

                dbManager.insert(name, desc);
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
                   /* if (cursor.moveToFirst()){
                        do{
                            String data = cursor.getString(cursor.getColumnIndex("subject"));
                            sb.append(data + ",");
                        }while(cursor.moveToNext());
                    }
                    sb.deleteCharAt(sb.length()-1);*/
                    appendUrl = sb.toString();
                    url = baseUrl + appendUrl;
                }
                else url = "https://source.unsplash.com/random";
                cursor.close();

                Toast.makeText(getApplicationContext(), url , Toast.LENGTH_LONG).show();


                //Toast.makeText(getApplicationContext(), "Set wallpaper task called", Toast.LENGTH_LONG).show();
                new SetWallpaperTask().execute();

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

            progressDialog = new ProgressDialog(AddMoodActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }
}