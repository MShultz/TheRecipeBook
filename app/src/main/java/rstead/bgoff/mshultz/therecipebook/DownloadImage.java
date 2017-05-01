package rstead.bgoff.mshultz.therecipebook;

/**
 * Created by Ben Goff on 5/1/2017.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {
        URL url;
        HttpURLConnection urlConnection;
        Bitmap bitmap = null;

        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();

            bitmap = BitmapFactory.decodeStream(inputStream);
        }catch(IOException e){
            Log.e("onCreate Error", e.toString());
        }
        return bitmap;
    }
}