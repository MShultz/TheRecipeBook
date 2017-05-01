package rstead.bgoff.mshultz.therecipebook;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.text.BoringLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
@TargetApi(25)
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    DatabaseHandler recipeDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRecipeDB(getDatabase());
		DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float den = metrics.density;
        int recipeSize = (int)(150 * den);
        int marg = (int)(15 * den);
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
        recipeLP.setMargins(marg,marg,marg,marg);

        for (int i = 0; i < 10; i++) {
            //every two recipes, add a new LinearLayout
            if(i % 2 == 0){
                currTab = new LinearLayout(this);
                currTab.setId(tabCount++);
                mainParent.addView(currTab);
            }

            recipe = new RecipeView(this);
            recipe.setLayoutParams(recipeLP);
            recipe.setcontent("BEEF");
            recipe.setsrcImage(getDrawable(R.drawable.beef));
            currTab.addView(recipe);
        }    }
    private DatabaseHandler getDatabase() {
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
                String imageURL = matcher.group(1);
                String name = matcher.group(2);
                String queryString = matcher.group(3);
                Log.i("Image URL", imageURL);
                Log.i("Name", name);
                Log.i("Query string", queryString);

                DownloadMaterial singlePageMaterial = new DownloadMaterial();
                String singlePageContent = singlePageMaterial.execute("http://allrecipes.com" + queryString).get();

                String ingredientRegex = "itemprop=\"ingredients\">(.+?)<\\/span>";
                Pattern ingredientsPattern = Pattern.compile(ingredientRegex);
                Matcher ingredientMatcher = ingredientsPattern.matcher(singlePageContent);

                ArrayList<String> ingredients = new ArrayList<>();
                while(ingredientMatcher.find()){
                    ingredients.add(ingredientMatcher.group(1));
                    Log.i("Ingredient", ingredientMatcher.group(1));
                }

                String directionsRegex = "recipe-directions__list--item\">(.+?)<\\/span>";
                Pattern directionsPattern = Pattern.compile(directionsRegex);
                Matcher directionsMatcher = directionsPattern.matcher(singlePageContent);

                ArrayList<String> directions = new ArrayList<>();
                while(directionsMatcher.find()){
                    directions.add(directionsMatcher.group(1));
                    Log.i("Direction", directionsMatcher.group(1));
                }

                String tipsRegex = "(?s)Footnotes.+?Tip.+?<li>(.+?)<\\/li>";
                Pattern tipsPattern = Pattern.compile(tipsRegex);
                Matcher tipsMatcher = tipsPattern.matcher(singlePageContent);

                ArrayList<String> tips = new ArrayList<>();
                while(tipsMatcher.find()){
                    tips.add(tipsMatcher.group(1));
                    Log.i("Tip", tipsMatcher.group(1));
                }

                String ingredientString = "";
                for(int i = 0; i < ingredients.size(); i++){
                    ingredientString += ingredients.get(i);
                    if(i != ingredients.size() - 1){
                        ingredientString += ",";
                    }
                }
                String directionsString = "";
                for(int i = 0; i < directions.size(); i++){
                    directionsString += directions.get(i);
                    if(i != directions.size() - 1){
                        directionsString += "\n";
                    }
                }
                String tipsString = "";
                for(int i = 0; i < tips.size(); i++){
                    tipsString += tips.get(i);
                    if(i != tips.size() - 1){
                        tipsString += "\n";
                    }
                }
            }
            Log.i("ContentDownloading", "Done!");
        }catch(InterruptedException | ExecutionException e){
            Log.e("Parse Error!", e.toString());
        }


        return recipes;
    }

}
