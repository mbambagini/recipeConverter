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
public class IngredientDAO {

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private String[] allIngredientsColumns = {
            DBHelper.COLUMN_INGREDIENTS_ID,
            DBHelper.COLUMN_INGREDIENTS_NAME,
            DBHelper.COLUMN_INGREDIENTS_QUANTITY,
            DBHelper.COLUMN_INGREDIENTS_UNIT
    };

    public IngredientDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

//    synchronized public void deleteClass (ClassEntry exam);

    synchronized public List<IngredientEntry> getIngredients (int idRecipe) {
        List<IngredientEntry> ingredients = new ArrayList<IngredientEntry>();
        String whereClause = new String (DBHelper.COLUMN_INGREDIENTS_ID_RECIPE+" = "+idRecipe);
        Cursor cursor = db.query(DBHelper.TABLE_RECIPES, allIngredientsColumns, whereClause, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ingredients.add(cursorToIngredient(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return ingredients;
    }

    //synchronized public void updateClass (ClassEntry cl) throws ClassNotFound {

    private IngredientEntry cursorToIngredient (Cursor cursor) {
        IngredientEntry ingredient = new IngredientEntry ();
        ingredient.setId(cursor.getInt(0));
        ingredient.setName(cursor.getString(1));
        ingredient.setQuantity(cursor.getInt(2));
        ingredient.setUnit(cursor.getInt(3));
        return ingredient;
    }

}
