package rstead.bgoff.mshultz.therecipebook;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    }

    private DatabaseHandler getDatabase() {
        return new DatabaseHandler(this.openOrCreateDatabase(DatabaseHandler.DB_NAME, MODE_PRIVATE, null));
    }

    public void setRecipeDB(DatabaseHandler recipeDB) {
        this.recipeDB = recipeDB;
    }
}
