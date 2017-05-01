package rstead.bgoff.mshultz.therecipebook;

/**
 * Created by Ben Goff on 5/1/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadMaterial extends AsyncTask<String, Void, String> {

    //Regex Pattern for main page:
    //(?s)grid-col__rec-image" data-lazy-load data-original-src="([\w:\-\/\.\?\=\&\;]*)".+?<h3\sclass="grid\-col__h3 grid\-col__h3\-\-recipe\-grid">.+?\s*([\w\d' ]*).+?<a href="([\w\d\/\-]*)

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(params[0]);
            urlConnection = (HttpURLConnection)url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            while(line != null){
                result += line;
                line = reader.readLine();
            }
        }catch(IOException e){
            Log.e("Some Error", e.toString());
            result = "Web search failed";
        }
        return result;
    }
}