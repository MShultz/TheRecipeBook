package rstead.bgoff.mshultz.therecipebook;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecipeDetailActivity extends AppCompatActivity {

    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_detail_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayList<String> originalAmounts = new ArrayList<>();

        final SeekBar recipeConverter = (SeekBar)findViewById(R.id.recipe_size_changer);
        recipeConverter.setProgress(1);
        recipeConverter.setMax(11);

        this.setRecipeDB(((GlobalHelper) this.getApplication()).getRecipeDB());
        recipeConverter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                LinearLayout ingredientParent = (LinearLayout)findViewById(R.id.ingredients_layout);

                for (int i = 0; i < ingredientParent.getChildCount(); i++){
                    TextView currentAmount = ((TextView)((LinearLayout)ingredientParent.getChildAt(i)).getChildAt(0));
                    originalAmounts.add(currentAmount.getText().toString());
                    Fraction frac = new Fraction(originalAmounts.get(i));
                    frac.scaleFraction(progress);
                    currentAmount.setText(frac.toString());
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        Recipe retrievedRecipe = dbHandler.getRecipe(getIntent().getIntExtra(MainActivity.EXTRA_ID, 1), getIntent().getBooleanExtra("isWeb", false));

        if(getIntent().getBooleanExtra("isWeb", false)){
            findViewById(R.id.edit_button).setVisibility(View.GONE);
            findViewById(R.id.del_button).setVisibility(View.GONE);

            try {
                ((ImageView) findViewById(R.id.recipe_Image)).setImageBitmap(new DownloadImage().execute(retrievedRecipe.getImageLink()).get());
            }catch(Exception e){};
            findViewById(R.id.recipe_Image).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.recipe_name)).setGravity(Gravity.CENTER);
        }
        showDetails(retrievedRecipe);

    }

    private void showDetails(Recipe retrievedRecipe) {

        int amountViewCount = 400;

        ((TextView)findViewById(R.id.recipe_name)).setText(retrievedRecipe.getName().trim());
        ((TextView)findViewById(R.id.directions_text)).setText(retrievedRecipe.getDescription().trim());
        ((TextView)findViewById(R.id.notes_text)).setText(retrievedRecipe.getNotes().trim());
        String ingredients = retrievedRecipe.getIngredients().trim();
        String[] ingredientList = ingredients.split(",");

        LinearLayout parent = (LinearLayout)findViewById(R.id.ingredients_layout);

        for(int i = 0; i < ingredientList.length; i++){
            String[] ingredientDetails = ingredientList[i].split(":");
            String amount = ingredientDetails[0];
            String remaining = " " + ingredientDetails[1] + " " + ingredientDetails[2];
            TextView amountView = new TextView(this);
            amountView.setId(amountViewCount++);
            amountView.setText(amount);
            TextView remainingIngredient = new TextView(this);
            remainingIngredient.setText(remaining);

            LinearLayout container = new LinearLayout(this);
            container.setOrientation(LinearLayout.HORIZONTAL);
            container.addView(amountView);
            container.addView(remainingIngredient);
            parent.addView(container);
        }
    }

    private void setRecipeDB(DatabaseHandler db){
        dbHandler = db;
    }

    public void onDeleteRecipe(View view){
        dbHandler.deleteRecipe(getIntent().getIntExtra(MainActivity.EXTRA_ID, 0));
        finish();
    }

}
