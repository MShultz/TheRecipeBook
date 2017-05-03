package rstead.bgoff.mshultz.therecipebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class InspirationActivity extends AppCompatActivity {

    public void setRecipeDB(DatabaseHandler recipeDB) {
        this.dbHandler = recipeDB;
    }


    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspiration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    private void initRecipes() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float den = metrics.density;
        int recipeSize = (int) (150 * den);
        int marg = (int) (15 * den);
        int tabCount = 1;

        LinearLayout mainParent = (LinearLayout) findViewById(R.id.mainLayout);

        LinearLayout currTab = new LinearLayout(this);
        LinearLayout.LayoutParams currTabParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        currTab.setLayoutParams(currTabParams);
        currTab.setId(tabCount);

        mainParent.addView(currTab);

        RecipeView recipe;
        RelativeLayout.LayoutParams recipeLP = new RelativeLayout.LayoutParams(recipeSize, recipeSize);
        recipeLP.setMargins(marg, marg, marg, marg);

        ArrayList<Recipe> recipes = recipeDB.getUserRecipes();

        for (int i = 0; i < recipes.size(); i++) {
            //every two recipes, add a new LinearLayout
            if (i % 2 == 0) {
                currTab = new LinearLayout(this);
                currTab.setId(tabCount++);
                mainParent.addView(currTab);
            }

            recipe = new RecipeView(this);
            recipe.setLayoutParams(recipeLP);

            recipe.setContent(recipes.get(i).getName());

            recipe.setRecipeKey(recipes.get(i).getId());
            recipe.setIsWeb(true);
            recipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToRecipeView(v);
                }
            });

            currTab.addView(recipe);
        }
    }

    private void sendToRecipeView(View view) {
        RecipeView recipe = (RecipeView) view;
        Log.e("RECIPE ID", recipe.getRecipeKey() + "");
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(MainActivity.EXTRA_ID, recipe.getRecipeKey());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
