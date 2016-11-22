package com.sashafierce.moodsplash;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by akanksha on 12/11/16.
 */

public class SetWallpaper extends AsyncTask<String, Void, Bitmap> {



    String url;
    Context thisActivity ;
    ProgressDialog progressDialog;
    Context context ;
    Context baseContext;
        public SetWallpaper(Context c , Context b , Context t) {
            context =  c;
            baseContext = b;
            thisActivity = t;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap result= null;

            try {
                result = Picasso.with(context)
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

            WallpaperManager wallpaperManager = WallpaperManager.getInstance(baseContext);
            try {
                wallpaperManager.setBitmap(result);

                Toast.makeText(context, "Set wallpaper successfully", Toast.LENGTH_LONG).show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();

            progressDialog = new ProgressDialog(thisActivity);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

}
