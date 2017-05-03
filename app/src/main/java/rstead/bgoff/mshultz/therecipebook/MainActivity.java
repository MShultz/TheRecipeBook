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
    private final String COMPLETE_PATTERN = "(?s)grid-col__rec-image\" data-lazy-load data-original-src=\"([\\w:\\-\\/\\.\\?\\=\\&\\;]*)\" (?!alt=\"Cook\").+?<h3 class=\"grid-col__h3 grid-col__h3--recipe-grid\">\\s*([\\w\\d'\\s\\-]*).+?<a href=\"(\\/recipe[\\w\\d\\/\\-]*)\"";
    private final String INGREDIENT_REGEX = "itemprop=\"ingredients\">(.+?)<\\/span>";
    private final String DIRECTION_REGEX = "recipe-directions__list--item\">(.+?)<\\/span>";
    private final String TIPS_REGEX = "(?s)Cook's Note.+?<li>(.+?)<\\/li>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRecipeDB(((GlobalHelper) this.getApplication()).getRecipeDB());
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

            recipe.setContent(recipes.get(i).getName());

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
        ((LinearLayout) findViewById(R.id.mainLayout)).removeAllViews();
        initRecipes();
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

    public ArrayList<Recipe> loadInspirations(){
        ArrayList<Recipe> recipes;
        boolean shouldLoad = recipeDB.shouldLoadFromDatabase();
        if(shouldLoad){
            recipes = recipeDB.getWebRecipes();
        }else{
            recipes = createRecipesFromHomePage();
        }
        return recipes;
    }

    public ArrayList<Recipe> createRecipesFromHomePage() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            StringBuilder pageContent = new StringBuilder().append(new DownloadMaterial().execute("http://allrecipes.com").get());
            Log.i("Content Downloading", "Downloading...");

            Pattern regexPattern = Pattern.compile(COMPLETE_PATTERN);
            Matcher matcher = regexPattern.matcher(pageContent.toString());

            DownloadMaterial singlePageMaterial;
            while (matcher.find()) {
                String queryString = matcher.group(3);
                singlePageMaterial = new DownloadMaterial();
                String singlePageContent = singlePageMaterial.execute("http://allrecipes.com" + queryString).get();

                recipes.add(new Recipe(matcher.group(2),
                        matcher.group(1),
                        getIngredientsStringFromPage(singlePageContent),
                        getDirectionsStringFromPage(singlePageContent),
                        getTipsStringFromPage(singlePageContent), new Date().toString()));
            }
            Log.i("ContentDownloading", "Done!");
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Parse Error!", e.toString());
        }


        return recipes;
    }

    private String getIngredientsStringFromPage(String pageContent) {
        Pattern ingredientPattern = Pattern.compile(INGREDIENT_REGEX);
        Matcher ingredientMatcher = ingredientPattern.matcher(pageContent);

        StringBuilder ingredientString = new StringBuilder();
        while (ingredientMatcher.find()) {
            ingredientString.append(ingredientMatcher.group(1));
            ingredientString.append(",");
        }
        ingredientString.setLength(ingredientString.length() - 1);
        return ingredientString.toString();
    }

    private String getDirectionsStringFromPage(String pageContent) {
        Pattern directionPattern = Pattern.compile(DIRECTION_REGEX);
        Matcher directionsMatcher = directionPattern.matcher(pageContent);

        StringBuilder directionString = new StringBuilder();
        while (directionsMatcher.find()) {
            directionString.append(directionsMatcher.group(1));
            directionString.append("\n");
        }
        directionString.setLength(directionString.length() - 1);
        return directionString.toString();
    }

    private String getTipsStringFromPage(String pageContent) {
        Pattern tipPattern = Pattern.compile(TIPS_REGEX);
        Matcher tipsMatcher = tipPattern.matcher(pageContent);

        StringBuilder tipString = new StringBuilder();
        while (tipsMatcher.find()) {
            tipString.append(tipsMatcher.group(1));
            tipString.append("\n");
        }
        if (tipString.length() > 0)
            tipString.setLength(tipString.length() - 1);
        return tipString.toString();
    }
}
