package rstead.bgoff.mshultz.therecipebook;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InspirationActivity extends AppCompatActivity {

    public void setRecipeDB(DatabaseHandler recipeDB) {
        this.dbHandler = recipeDB;
    }

    private final String COMPLETE_PATTERN = "(?s)grid-col__rec-image\" data-lazy-load data-original-src=\"([\\w:\\-\\/\\.\\?\\=\\&\\;]*)\" (?!alt=\"Cook\").+?<h3 class=\"grid-col__h3 grid-col__h3--recipe-grid\">\\s*([\\w\\d '\\-]*).+?<a href=\"(\\/recipe[\\w\\d\\/\\-]*)\"";
    private final String INGREDIENT_REGEX = "itemprop=\"ingredients\">(.+?)<\\/span>";
    private final String DIRECTION_REGEX = "recipe-directions__list--item\">(.+?)<\\/span>";
    private final String TIPS_REGEX = "(?s)Cook's Note.+?<li>(.+?)<\\/li>";
    private final String INGREDIENT_SPLIT_REGEX = "([\\d]* [\\d\\/]*|[\\d]*) (.+?[\\w\\d(),]*) ?(.+[\\w ,]*)";
    private Pattern directionPattern = Pattern.compile(DIRECTION_REGEX);
    private Pattern tipPattern = Pattern.compile(TIPS_REGEX);
    private Pattern ingredientPattern = Pattern.compile(INGREDIENT_REGEX);
    private Pattern ingredientSplitPattern = Pattern.compile(INGREDIENT_SPLIT_REGEX);

    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspiration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRecipeDB(((GlobalHelper) this.getApplication()).getRecipeDB());
        initRecipes();

    }

    @TargetApi(25)
    private void initRecipes() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float den = metrics.density;
        int recipeSize = (int) (150 * den);
        int marg = (int) (15 * den);
        int tabCount = 1;

        ArrayList<Recipe> recipes = loadInspirations();

        LinearLayout mainParent = (LinearLayout) findViewById(R.id.mainWebLayout);

        LinearLayout currTab = new LinearLayout(this);
        LinearLayout.LayoutParams currTabParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        currTab.setLayoutParams(currTabParams);
        currTab.setId(tabCount);

        mainParent.addView(currTab);

        RecipeView recipe;
        RelativeLayout.LayoutParams recipeLP = new RelativeLayout.LayoutParams(recipeSize, recipeSize);

        recipeLP.setMargins(marg, marg, marg, marg);

        for (int i = 0; i < recipes.size(); i++) {
            //every two recipes, add a new LinearLayout
            if (i % 2 == 0) {
                currTab = new LinearLayout(this);
                currTab.setLayoutParams(currTabParams);
                currTab.setId(tabCount++);
                mainParent.addView(currTab);
            }

            recipe = new RecipeView(this);
            recipe.setLayoutParams(recipeLP);

            recipe.setContent(recipes.get(i).getName().trim());
            try {
                recipe.setsrcImage(new BitmapDrawable(new WebImage().execute(recipes.get(i).getImageLink()).get()));
            }catch(Exception e){

            }
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
        intent.putExtra("isWeb", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public ArrayList<Recipe> loadInspirations(){
        ArrayList<Recipe> recipes;
        boolean shouldLoad = dbHandler.shouldLoadFromDatabase();
        if(shouldLoad){
            recipes = dbHandler.getWebRecipes();
        }else{
            recipes = createRecipesFromHomePage();
        }
        return recipes;
    }

    public ArrayList<Recipe> createRecipesFromHomePage() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            StringBuilder pageContent = new StringBuilder().append(new DownloadMaterial().execute("http://allrecipes.com").get());

            Pattern regexPattern = Pattern.compile(COMPLETE_PATTERN);
            Matcher matcher = regexPattern.matcher(pageContent.toString());

            DownloadMaterial singlePageMaterial;
            while (matcher.find()) {
                singlePageMaterial = new DownloadMaterial();
                String singlePageContent = singlePageMaterial.execute("http://allrecipes.com" + matcher.group(3)).get();

                recipes.add(new Recipe(matcher.group(2),
                        matcher.group(1),
                        getIngredientsStringFromPage(singlePageContent),
                        getDirectionsStringFromPage(singlePageContent),
                        getTipsStringFromPage(singlePageContent), new SimpleDateFormat("MM/dd/yy HH:mm:ss").format(new Date())));
            }
        } catch (InterruptedException | ExecutionException e) {
            Log.e("Parse Error!", e.toString());
        }
        recipes = dbHandler.resetWebRecipes(recipes);

        return recipes;
    }


    private String getIngredientsStringFromPage(String pageContent) {
        Matcher ingredientMatcher = ingredientPattern.matcher(pageContent);

        StringBuilder ingredientString = new StringBuilder();
        while (ingredientMatcher.find()) {
            ingredientString.append(createSeparatedString(ingredientMatcher.group(1)));
            ingredientString.append(",");
        }
        ingredientString.setLength(ingredientString.length() - 1);
        return ingredientString.toString();
    }

    private String createSeparatedString(String originalString){
        Matcher ingredientStringMatcher = ingredientSplitPattern.matcher(originalString);
        StringBuilder stringBuilder = new StringBuilder();
        while(ingredientStringMatcher.find()){
            stringBuilder.append(ingredientStringMatcher.group(1));
            stringBuilder.append(":");
            stringBuilder.append(ingredientStringMatcher.group(2).replace(",", ""));
            stringBuilder.append(":");
            stringBuilder.append(ingredientStringMatcher.group(3).replace(",", ""));
        }
        return stringBuilder.toString();
    }

    private String getDirectionsStringFromPage(String pageContent) {
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
