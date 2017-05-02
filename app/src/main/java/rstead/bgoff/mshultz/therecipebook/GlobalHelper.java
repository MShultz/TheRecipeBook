package rstead.bgoff.mshultz.therecipebook;

import android.app.Application;

/**
 * Created by Mary on 5/2/2017.
 */

public class GlobalHelper extends Application {
    DatabaseHandler handle;
    @Override
    public void onCreate(){
        super.onCreate();
        handle = new DatabaseHandler(getApplicationContext().openOrCreateDatabase(DatabaseHandler.DB_NAME, MODE_PRIVATE, null));
    }

    public DatabaseHandler getRecipeDB(){
        return handle;
    }

}
