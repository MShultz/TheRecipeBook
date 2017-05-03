package rstead.bgoff.mshultz.therecipebook;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Mary on 5/1/2017.
 */

public class DatabaseHandler {
    public final static String DB_NAME = "RecipeBook";
    public final String PK_ID = "ID";
    public final String TABLE_NAME = "recipes";
    private final String NAME_COL = "name";
    private final String INGREDIENTS_COL = "ingredients";
    private final String DESCRIPTION_COL = "description";
    private final String NOTES_COL = "notes";
    private final String IMAGE_COL = "imageLink";
    private SQLiteDatabase recipeBookDatabase;
    private Cursor cursor;
    private int name, ingredients, description, notes, imageLink, id;


    public DatabaseHandler(SQLiteDatabase recipeBookDatabase) {
        this.setRecipeBookDatabase(recipeBookDatabase);
        initializeRecipesTable();
    }

    private void initializeRecipesTable() {
        recipeBookDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + NAME_COL + " varchar, "
                + IMAGE_COL + " varchar, "
                + INGREDIENTS_COL + " varchar, "
                + DESCRIPTION_COL + " varchar, "
                + NOTES_COL + " varchar)");

    }


    private void setRecipeBookDatabase(SQLiteDatabase recipeBookDatabase) {
        this.recipeBookDatabase = recipeBookDatabase;
    }

    public ArrayList<Recipe> getAllRecipes() {
        ArrayList<Recipe> list = new ArrayList<>();
        cursor = recipeBookDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                list.add(createRecipe(cursor));
            } while (cursor.moveToNext());
        }
        return list;
    }

    private Recipe createRecipe(Cursor cursor) {
            id = cursor.getColumnIndex(PK_ID);
            name = cursor.getColumnIndex(NAME_COL);
            imageLink = cursor.getColumnIndex(IMAGE_COL);
            ingredients = cursor.getColumnIndex(INGREDIENTS_COL);
            description = cursor.getColumnIndex(DESCRIPTION_COL);
            notes = cursor.getColumnIndex(NOTES_COL);
            return new Recipe(Integer.parseInt(cursor.getString(id)),
                    cursor.getString(name),
                    cursor.getString(imageLink),
                    cursor.getString(ingredients),
                    cursor.getString(description),
                    cursor.getString(notes));
    }

    public void addRecipe(Recipe recipe) {
        recipeBookDatabase.insert(TABLE_NAME, null, getRecipeContentValues(recipe));

    }

    private ContentValues getRecipeContentValues(Recipe recipe) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(NAME_COL, recipe.getName());
        recipeValues.put(IMAGE_COL, recipe.getImageLink());
        recipeValues.put(INGREDIENTS_COL, recipe.getIngredients());
        recipeValues.put(DESCRIPTION_COL, recipe.getDescription());
        recipeValues.put(NOTES_COL, recipe.getNotes());
        return recipeValues;
    }

    public void deleteRecipe(int pk) {
        recipeBookDatabase.delete(TABLE_NAME, PK_ID + " = " + pk, null);
    }

    public boolean updateItem(int id, Recipe editedRecipe) {
        int success = recipeBookDatabase.update(TABLE_NAME, getRecipeContentValues(editedRecipe), PK_ID + " = " + id, null);
        return (success > 0);
    }

    public void clearDatabase() {
        recipeBookDatabase.delete(TABLE_NAME, null, null);
    }

    public Recipe getRecipe(int pk) {
        Cursor cursor = recipeBookDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + PK_ID + " = " + pk, null);
        cursor.moveToFirst();
        return createRecipe(cursor);
    }
}
