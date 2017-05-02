package rstead.bgoff.mshultz.therecipebook;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.Toast;

@TargetApi(25)
public class MainActivity extends AppCompatActivity {
    DatabaseHandler recipeDB;
private static final String EXTRA_IMAGE = "rstead.bgoff.mshultz.therecipebook.IMAGE";
    private static final String EXTRA_LINK = "rstead.bgoff.mshultz.therecipebook.LINK";
    private static final String EXTRA_ID = "rstead.bgoff.mshultz.therecipebook.ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRecipeDB(getDatabase());

        initRecipes();
    }
    private void initRecipes() {
        DisplayMetrics metrics = new DisplayMetrics();        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float den = metrics.density;
        int recipeSize = (int) (150 * den);
        int marg = (int) (15 * den);
        int tabCount = 1;
		createRecipesFromHomePage();

        LinearLayout mainParent = (LinearLayout) findViewById(R.id.mainLayout);

        LinearLayout currTab = new LinearLayout(this);
        LinearLayout.LayoutParams currTabParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        currTab.setLayoutParams(currTabParams);
        currTab.setId(tabCount);

        mainParent.addView(currTab);

        RecipeView recipe;
        RelativeLayout.LayoutParams recipeLP = new RelativeLayout.LayoutParams(recipeSize, recipeSize);
        recipeLP.setMargins(marg, marg, marg, marg);

        for (int i = 0; i < 10; i++) {
            //every two recipes, add a new LinearLayout
            if (i % 2 == 0) {
                currTab = new LinearLayout(this);
                currTab.setId(tabCount++);
                mainParent.addView(currTab);
            }

            recipe = new RecipeView(this);
            recipe.setLayoutParams(recipeLP);

            //add the recipe's contents
            recipe.setContent("BEEF");
            recipe.setsrcImage(getDrawable(R.drawable.beef));
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
    private void sendToRecipeView(View view) {
        RecipeView recipe = (RecipeView)view;
        Intent intent = new Intent(this, RecipeViewActivity.class);
        intent.putExtra(EXTRA_ID, recipe.getRecipeKey());
        startActivity(intent);
        //Toast.makeText(this, "Hello there, it worked!", Toast.LENGTH_SHORT).show();
    }    private DatabaseHandler getDatabase() {
        return new DatabaseHandler(this.openOrCreateDatabase(DatabaseHandler.DB_NAME, MODE_PRIVATE, null));
    }

    public void setRecipeDB(DatabaseHandler recipeDB) {
        this.recipeDB = recipeDB;
    }

    public ArrayList<Recipe> createRecipesFromHomePage(){
        ArrayList<Recipe> recipes = new ArrayList<>();
        DownloadMaterial downloadMaterial = new DownloadMaterial();
        try{
            String pageContent = downloadMaterial.execute("http://allrecipes.com").get();
            Log.i("Content Downloading", "Downloading...");
            String pattern = "(?s)grid-col__rec-image\" data-lazy-load data-original-src=\"([\\w:\\-\\/\\.\\?\\=\\&\\;]*)\".+?<h3\\sclass=\"grid\\-col__h3 grid\\-col__h3\\-\\-recipe\\-grid\">.+?\\s*([\\w\\d'\\s]*).+?<a href=\"(\\/recipe[\\w\\d\\/\\-]*)";

            Pattern regexPattern = Pattern.compile(pattern);
            Matcher matcher = regexPattern.matcher(pageContent);

            while(matcher.find()){
                String queryString = matcher.group(3);

                DownloadMaterial singlePageMaterial = new DownloadMaterial();
                String singlePageContent = singlePageMaterial.execute("http://allrecipes.com" + queryString).get();

                String name = matcher.group(2);
                String imageURL = matcher.group(1);
                String ingredientsString = getIngredientsStringFromPage(singlePageContent);
                String directionsString = getDirectionsStringFromPage(singlePageContent);
                String tipsString = getTipsStringFromPage(singlePageContent);

                Recipe recipe = new Recipe(name, imageURL, ingredientsString, directionsString, tipsString);
                recipes.add(recipe);

                Log.i("Image URL", imageURL);
                Log.i("Name", name);
                Log.i("Query string", queryString);

            }
            Log.i("ContentDownloading", "Done!");
        }catch(InterruptedException | ExecutionException e){
            Log.e("Parse Error!", e.toString());
        }


        return recipes;
    }

    private String getIngredientsStringFromPage(String pageContent){
        String ingredientsString = "";

        String ingredientRegex = "itemprop=\"ingredients\">(.+?)<\\/span>";
        Pattern ingredientsPattern = Pattern.compile(ingredientRegex);
        Matcher ingredientMatcher = ingredientsPattern.matcher(pageContent);

        ArrayList<String> ingredients = new ArrayList<>();
        while(ingredientMatcher.find()){
            ingredients.add(ingredientMatcher.group(1));
            Log.i("Ingredient", ingredientMatcher.group(1));
        }

        for(int i = 0; i < ingredients.size(); i++){
            ingredientsString += ingredients.get(i);
            if(i != ingredients.size() - 1){
                ingredientsString += ",";
            }
        }
        return ingredientsString;
    }

    private String getDirectionsStringFromPage(String pageContent){
        String directionsString = "";

        String directionsRegex = "recipe-directions__list--item\">(.+?)<\\/span>";
        Pattern directionsPattern = Pattern.compile(directionsRegex);
        Matcher directionsMatcher = directionsPattern.matcher(pageContent);

        ArrayList<String> directions = new ArrayList<>();
        while(directionsMatcher.find()){
            directions.add(directionsMatcher.group(1));
            Log.i("Direction", directionsMatcher.group(1));
        }

        for(int i = 0; i < directions.size(); i++){
            directionsString += directions.get(i);
            if(i != directions.size() - 1){
                directionsString += "\n";
            }
        }
        return directionsString;
    }

    private String getTipsStringFromPage(String pageContent){
        String tipsString = "";

        String tipsRegex = "(?s)Footnotes.+?Tip.+?<li>(.+?)<\\/li>";
        Pattern tipsPattern = Pattern.compile(tipsRegex);
        Matcher tipsMatcher = tipsPattern.matcher(pageContent);

        ArrayList<String> tips = new ArrayList<>();
        while(tipsMatcher.find()){
            tips.add(tipsMatcher.group(1));
            Log.i("Tip", tipsMatcher.group(1));
        }

        for(int i = 0; i < tips.size(); i++){
            tipsString += tips.get(i);
            if(i != tips.size() - 1){
                tipsString += "\n";
            }
        }
        return tipsString;
    }

}
