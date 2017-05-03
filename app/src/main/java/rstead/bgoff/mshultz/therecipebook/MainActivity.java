package rstead.bgoff.mshultz.therecipebook;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TargetApi(25)
public class MainActivity extends FragmentActivity implements AddRecipeDialogue.AddRecipeListener {
    DatabaseHandler recipeDB;

    private static final String EXTRA_IMAGE = "rstead.bgoff.mshultz.therecipebook.IMAGE";
    private static final String EXTRA_LINK = "rstead.bgoff.mshultz.therecipebook.LINK";
    public static final String EXTRA_ID = "rstead.bgoff.mshultz.therecipebook.ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        this.setRecipeDB(((GlobalHelper) this.getApplication()).getRecipeDB());
        refreshRecipes();
    }

    private void refreshRecipes() {
        ((LinearLayout) findViewById(R.id.mainLayout)).removeAllViews();
        initRecipes();
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

            recipe.setContent(recipes.get(i).getName().trim());

            recipe.setRecipeKey(recipes.get(i).getId());
            recipe.setIsWeb(false);
            recipe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToRecipeView(v);
                }
            });

            currTab.addView(recipe);
        }
    }

    @Override
    public void onDoneClick(DialogFragment diagFrag) {
        AddRecipeDialogue dialogue = (AddRecipeDialogue) diagFrag;
        Recipe newRecipe = dialogue.createRecipe();
        recipeDB.addRecipe(newRecipe);
        refreshRecipes();
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshRecipes();
    }

    public void goToInspirations(View view){
        Intent intent = new Intent(this, InspirationActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void sendToRecipeView(View view) {
        RecipeView recipe = (RecipeView) view;
        Log.e("RECIPE ID", recipe.getRecipeKey() + "");
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        intent.putExtra(EXTRA_ID, recipe.getRecipeKey());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void onAddClick(View view) {
        DialogFragment dialogue = new AddRecipeDialogue();
        dialogue.show(getFragmentManager(), "Add");
    }

    public void setRecipeDB(DatabaseHandler recipeDB) {
        this.recipeDB = recipeDB;
    }

}
