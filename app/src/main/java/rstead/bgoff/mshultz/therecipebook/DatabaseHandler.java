package rstead.bgoff.mshultz.therecipebook;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mary on 5/1/2017.
 */

public class DatabaseHandler {
    public final static String DB_NAME = "RecipeBook";
    public final String PK_ID = "ID";
    public final String USER_TABLE = "recipes";
    public final String WEB_TABLE = "webrecipes";
    private final String NAME_COL = "name";
    private final String INGREDIENTS_COL = "ingredients";
    private final String DESCRIPTION_COL = "description";
    private final String NOTES_COL = "notes";
    private final String IMAGE_COL = "imageLink";
    private final String DATE_COL = "dateadded";
    private final long DATABASE_HOURS_THRESHOLD = 24;
    private final long MILLISECONDS_IN_HOUR = 3600000;
    private SQLiteDatabase recipeBookDatabase;
    private Cursor cursor;


    public DatabaseHandler(SQLiteDatabase recipeBookDatabase) {
        this.setRecipeBookDatabase(recipeBookDatabase);
        initializeRecipeTables();
    }

    private void initializeRecipeTables() {
        recipeBookDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + USER_TABLE + "("
                + PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + NAME_COL + " varchar, "
                + IMAGE_COL + " varchar, "
                + INGREDIENTS_COL + " varchar, "
                + DESCRIPTION_COL + " varchar, "
                + NOTES_COL + " varchar, "
                + DATE_COL + " datetime)");

        recipeBookDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + WEB_TABLE + "("
                + PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + NAME_COL + " varchar, "
                + IMAGE_COL + " varchar, "
                + INGREDIENTS_COL + " varchar, "
                + DESCRIPTION_COL + " varchar, "
                + NOTES_COL + " varchar, "
                + DATE_COL + " datetime)");

    }


    private void setRecipeBookDatabase(SQLiteDatabase recipeBookDatabase) {
        this.recipeBookDatabase = recipeBookDatabase;
    }

    public ArrayList<Recipe> getUserRecipes() {
        cursor = recipeBookDatabase.rawQuery("SELECT * FROM " + USER_TABLE, null);
        return populateRecipeList(cursor);
    }

    public ArrayList<Recipe> getWebRecipes() {
        cursor = recipeBookDatabase.rawQuery("SELECT * FROM " + WEB_TABLE, null);
        return populateRecipeList(cursor);
    }

    public ArrayList<Recipe> populateRecipeList(Cursor cursor){
        ArrayList<Recipe> list = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                list.add(createRecipe(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }

    private Recipe createRecipe(Cursor cursor) {
            int id = cursor.getColumnIndex(PK_ID);
            int name = cursor.getColumnIndex(NAME_COL);
            int imageLink = cursor.getColumnIndex(IMAGE_COL);
            int ingredients = cursor.getColumnIndex(INGREDIENTS_COL);
            int description = cursor.getColumnIndex(DESCRIPTION_COL);
            int notes = cursor.getColumnIndex(NOTES_COL);
            int dateCreated = cursor.getColumnIndex(DATE_COL);
            return new Recipe(Integer.parseInt(cursor.getString(id)),
                    cursor.getString(name),
                    cursor.getString(imageLink),
                    cursor.getString(ingredients),
                    cursor.getString(description),
                    cursor.getString(notes),
                    cursor.getString(dateCreated));
    }

    public void addRecipe(Recipe recipe, boolean isWeb) {
        recipeBookDatabase.insert(isWeb ? WEB_TABLE : USER_TABLE, null, getRecipeContentValues(recipe));

    }

    public ArrayList<Recipe> resetWebRecipes(ArrayList<Recipe> recipes){
        recipeBookDatabase.delete(WEB_TABLE, null, null);
        for(Recipe recipe : recipes){
            Log.e("WEBRECIPELOG:", recipe.getImageLink());
            addRecipe(recipe, true);
        }
        return getWebRecipes();
    }

    private ContentValues getRecipeContentValues(Recipe recipe) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(NAME_COL, recipe.getName());
        recipeValues.put(IMAGE_COL, recipe.getImageLink());
        recipeValues.put(INGREDIENTS_COL, recipe.getIngredients());
        recipeValues.put(DESCRIPTION_COL, recipe.getDescription());
        recipeValues.put(NOTES_COL, recipe.getNotes());
        recipeValues.put(DATE_COL, recipe.getDateCreated());
        return recipeValues;
    }

    public void deleteRecipe(int pk) {
        recipeBookDatabase.delete(USER_TABLE, PK_ID + " = " + pk, null);
    }

    public boolean updateItem(int id, Recipe editedRecipe) {
        int success = recipeBookDatabase.update(USER_TABLE, getRecipeContentValues(editedRecipe), PK_ID + " = " + id, null);
        return (success > 0);
    }

    public void clearDatabase() {
        recipeBookDatabase.delete(USER_TABLE, null, null);
    }

    public Recipe getRecipe(int pk, boolean isWeb) {
        Cursor cursor = recipeBookDatabase.rawQuery("SELECT * FROM " + (isWeb ? WEB_TABLE : USER_TABLE) + " WHERE " + PK_ID + " = " + pk, null);
        cursor.moveToFirst();
        return createRecipe(cursor);
    }

    public boolean shouldLoadFromDatabase(){
        Cursor cursor = recipeBookDatabase.rawQuery("SELECT * FROM " + WEB_TABLE, null);
        boolean shouldLoad = false;
        if (cursor.getCount() > 0) {
            try{
                cursor.moveToFirst();
                Date d = new Date();
                dateCreated = cursor.getColumnIndex(DATE_COL);
                Date lastDate = new SimpleDateFormat("MM/dd/yy HH:mm:ss").parse(cursor.getString(dateCreated));
                long hoursPassed = (d.getTime() - lastDate.getTime()) / MILLISECONDS_IN_HOUR;
                Log.e("HOURS", hoursPassed + "");
                if(hoursPassed < DATABASE_HOURS_THRESHOLD){
                    shouldLoad = true;
                }
            }catch(ParseException e){
                Log.e("ERROR STORING DATE", e.toString());
            }
        }
        return shouldLoad;
    }
}
