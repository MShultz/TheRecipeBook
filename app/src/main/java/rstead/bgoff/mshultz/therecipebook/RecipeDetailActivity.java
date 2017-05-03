package rstead.bgoff.mshultz.therecipebook;

import android.app.DialogFragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class RecipeDetailActivity extends AppCompatActivity {

    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setRecipeDB(((GlobalHelper) this.getApplication()).getRecipeDB());

        Recipe retrievedRecipe = dbHandler.getRecipe(getIntent().getIntExtra(MainActivity.EXTRA_ID, 1));

        showDetails(retrievedRecipe);

    }

    private void showDetails(Recipe retrievedRecipe) {
        ((TextView)findViewById(R.id.recipe_name)).setText(retrievedRecipe.getName());
        ((TextView)findViewById(R.id.directions_text)).setText(retrievedRecipe.getDescription());
        ((TextView)findViewById(R.id.notes_text)).setText(retrievedRecipe.getNotes());

    }


    private void setRecipeDB(DatabaseHandler db){
        dbHandler = db;
    }

}
