package com.sashafierce.moodsplash;

        import android.app.ProgressDialog;
        import android.app.WallpaperManager;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.graphics.Bitmap;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.squareup.picasso.Picasso;

        import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    Bitmap result;
    String url;
    StringBuilder sb;
    String appendUrl;
    int length , height ;
    String baseUrl = "https://source.unsplash.com/600x750/?";
    Cursor cursor;

    private DatabaseHelper databaseHelper;
    private long _id;
    private DBManager dbManager;
    ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbManager = new DBManager(this);
        dbManager.open();

        databaseHelper = new DatabaseHelper(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }


    //called when settings button clicked
    public void setting(View view) {
        Intent intent = new Intent(this, Setting.class);
        startActivity(intent);
    }
    //called when edit mood button clicked
    public void editMood(View view) {
        Intent intent = new Intent(this, MoodListActivity.class);

        startActivity(intent);


    }
    public void random(View view) {

        url = "https://source.unsplash.com/random";
        if(isOnline()){
            new SetWallpaperTask().execute();
        } else{
            Toast.makeText(getApplicationContext(),"Error Connecting to server",Toast.LENGTH_LONG).show();

        }


    }
    public void follow(View view) {
        Intent intent = new Intent(this, FollowActivity.class);

        startActivity(intent);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.sign_out) {

            auth.signOut();

            //Toast.makeText(getApplicationContext(),"Signout method",Toast.LENGTH_LONG).show();
            FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // user auth state is changed - user is null
                        // launch login activity
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            };
            auth.addAuthStateListener(authListener);

        }
        else if(id == R.id.shuffle) {

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
            if(isOnline()){
                new SetWallpaperTask().execute();
            } else{
                Toast.makeText(getApplicationContext(),"Error Connecting to server",Toast.LENGTH_LONG).show();

            }

        }
        return super.onOptionsItemSelected(item);
    }
    public class SetWallpaperTask extends AsyncTask<String, Void, Bitmap> {

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
            progressDialog.dismiss();
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

            progressDialog = new ProgressDialog(MainActivity.this);
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