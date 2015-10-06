package recipeconverter.org.recipeconverter.recipecomverter.org.recipeconverter.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public RecipeDAO (Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

//    synchronized public void deleteClass (ClassEntry exam);

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

    //synchronized public void updateClass (ClassEntry cl) throws ClassNotFound {

    private RecipeEntry cursorToRecipe (Cursor cursor) {
        RecipeEntry recipe = new RecipeEntry ();
        recipe.setId(cursor.getInt(0));
        recipe.setName(cursor.getString(1));
        recipe.setNum_people(cursor.getInt(2));
        recipe.setShape(cursor.getInt(3));
        recipe.setSide1(cursor.getInt(4));
        recipe.setSide2(cursor.getInt(5));
        recipe.setDiameter(cursor.getInt(6));
        recipe.setType(cursor.getInt(7));
        return recipe;
    }

}
