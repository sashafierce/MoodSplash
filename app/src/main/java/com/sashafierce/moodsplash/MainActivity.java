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
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Toast;

        import com.squareup.picasso.Picasso;

        import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    Bitmap result;
    String url;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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