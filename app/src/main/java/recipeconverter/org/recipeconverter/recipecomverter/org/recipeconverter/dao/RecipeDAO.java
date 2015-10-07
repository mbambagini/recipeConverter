package recipeconverter.org.recipeconverter.recipecomverter.org.recipeconverter.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import recipeconverter.org.recipeconverter.exception.EntryError;
import recipeconverter.org.recipeconverter.exception.EntryNotFound;

/**
 * Created by mario on 06/10/15.
 */
public class RecipeDAO {

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private String[] allRecipeColumns = {
            DBHelper.COLUMN_RECIPES_ID,
            DBHelper.COLUMN_RECIPES_NAME,
            DBHelper.COLUMN_RECIPES_PEOPLE_NUMBER,
            DBHelper.COLUMN_RECIPES_SHAPE,
            DBHelper.COLUMN_RECIPES_SIDE_1,
            DBHelper.COLUMN_RECIPES_SIDE_2,
            DBHelper.COLUMN_RECIPES_DIAMETER,
            DBHelper.COLUMN_RECIPES_TYPE};

    private String[] allIngredientsColumns = {
            DBHelper.COLUMN_INGREDIENTS_ID,
            DBHelper.COLUMN_INGREDIENTS_NAME,
            DBHelper.COLUMN_INGREDIENTS_QUANTITY,
            DBHelper.COLUMN_INGREDIENTS_UNIT
    };

    public RecipeDAO (Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    synchronized public List<RecipeEntry> getRecipes () {
        List<RecipeEntry> recipes = new ArrayList<RecipeEntry>();
        Cursor cursor = db.query(DBHelper.TABLE_RECIPES, allRecipeColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            recipes.add(cursorToRecipe(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return recipes;
    }

    synchronized public RecipeEntry getRecipe (int id) throws EntryNotFound, EntryError {
        String whereClause = new String (DBHelper.COLUMN_RECIPES_ID + " = " + id);
        Cursor cursor = db.query(DBHelper.TABLE_RECIPES, allRecipeColumns, whereClause, null, null, null, null);
        if (cursor.getCount() == 0)
            throw new EntryNotFound();
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            RecipeEntry recipe = cursorToRecipe(cursor);
            recipe.setIngredients(getIngredients(recipe.getId()));
            return recipe;
        }
        throw new EntryError();
    }

    synchronized public void updateRecipe (RecipeEntry cl) {

    }

    synchronized public void deleteRecipe (int id) {

    }

    synchronized private List<IngredientEntry> getIngredients (long idRecipe) {
        List<IngredientEntry> ingredients = new ArrayList<IngredientEntry>();
        String whereClause = new String (DBHelper.COLUMN_INGREDIENTS_ID_RECIPE + " = " + idRecipe);
        Cursor cursor = db.query(DBHelper.TABLE_RECIPES, allIngredientsColumns, whereClause, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ingredients.add(cursorToIngredient(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return ingredients;
    }

    private RecipeEntry cursorToRecipe (Cursor cursor) {
        RecipeEntry recipe = new RecipeEntry ();
        recipe.setId(cursor.getLong(0));
        recipe.setName(cursor.getString(1));
        recipe.setNum_people(cursor.getInt(2));
        recipe.setShape(cursor.getInt(3));
        recipe.setSide1(cursor.getInt(4));
        recipe.setSide2(cursor.getInt(5));
        recipe.setDiameter(cursor.getInt(6));
        recipe.setType(cursor.getInt(7));
        return recipe;
    }

    private IngredientEntry cursorToIngredient (Cursor cursor) {
        IngredientEntry ingredient = new IngredientEntry ();
        ingredient.setId(cursor.getLong(0));
        ingredient.setName(cursor.getString(1));
        ingredient.setQuantity(cursor.getInt(2));
        ingredient.setUnit(cursor.getInt(3));
        return ingredient;
    }

}
