package recipeconverter.org.recipeconverter.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import recipeconverter.org.recipeconverter.exception.EntryError;
import recipeconverter.org.recipeconverter.exception.EntryNotFound;
import recipeconverter.org.recipeconverter.exception.RecipeAlreadyPresent;
import recipeconverter.org.recipeconverter.exception.RecipeNotCreated;

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
            DBHelper.COLUMN_RECIPES_DIM_UNIT};

    private String[] allIngredientsColumns = {
            DBHelper.COLUMN_INGREDIENTS_ID,
            DBHelper.COLUMN_INGREDIENTS_NAME,
            DBHelper.COLUMN_INGREDIENTS_QUANTITY,
            DBHelper.COLUMN_INGREDIENTS_UNIT
    };

    public RecipeDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    synchronized public List<RecipeEntry> getRecipes(String name) {
        List<RecipeEntry> recipes = new ArrayList<RecipeEntry>();
        String whereClause = null;

        if (name != null && !name.isEmpty())
            whereClause = DBHelper.COLUMN_RECIPES_NAME + " like \"%" + name + "%\"";
        Cursor cursor = db.query(DBHelper.TABLE_RECIPES, allRecipeColumns, whereClause, null, null, null, DBHelper.COLUMN_RECIPES_ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            recipes.add(cursorToRecipe(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return recipes;
    }

    synchronized public RecipeEntry getRecipe(long id) throws EntryNotFound, EntryError {
        String whereClause = DBHelper.COLUMN_RECIPES_ID + " = " + id;
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

    private void addIngredient(IngredientEntry ingredient, long idRecipe) throws EntryError {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_INGREDIENTS_NAME, ingredient.getName());
        values.put(DBHelper.COLUMN_INGREDIENTS_QUANTITY, ingredient.getQuantity());
        values.put(DBHelper.COLUMN_INGREDIENTS_UNIT, UnitType.toInteger(ingredient.getUnit()));
        values.put(DBHelper.COLUMN_INGREDIENTS_ID_RECIPE, idRecipe);
        db.insert(DBHelper.TABLE_INGREDIENTS, null, values);
    }

    public boolean recipeAlreadyPresent(String name) {
        return countInstances(name) != 0;
    }

    private int countInstances(String name) {
        String whereClause = DBHelper.COLUMN_RECIPES_NAME + " = \"" + name + "\"";
        Cursor cursor = db.query(DBHelper.TABLE_RECIPES, allRecipeColumns, whereClause, null, null, null, null);
        int ret = cursor.getCount();
        cursor.close();
        return ret;
    }

    synchronized public long addRecipe(RecipeEntry recipe) throws RecipeNotCreated, RecipeAlreadyPresent {
        if (countInstances(recipe.getName()) != 0)
            throw new RecipeAlreadyPresent();

        long id;
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_RECIPES_NAME, recipe.getName());
        values.put(DBHelper.COLUMN_RECIPES_PEOPLE_NUMBER, recipe.getNum_people());
        values.put(DBHelper.COLUMN_RECIPES_SHAPE, ShapeType.toInteger(recipe.getShape()));
        if (recipe.isRecipeWRTPan() && recipe.getShape()==ShapeType.SHAPE_SQUARE)
            values.put(DBHelper.COLUMN_RECIPES_SIDE_1, recipe.getSideSQ());
        else
            values.put(DBHelper.COLUMN_RECIPES_SIDE_1, recipe.getSideL1());
        values.put(DBHelper.COLUMN_RECIPES_SIDE_2, recipe.getSideL2());
        values.put(DBHelper.COLUMN_RECIPES_DIAMETER, recipe.getDiameter());
        values.put(DBHelper.COLUMN_RECIPES_DIM_UNIT, recipe.getDimUnit());
        try {
            id = db.insert(DBHelper.TABLE_RECIPES, null, values);
        } catch (Exception e) {
            throw new RecipeNotCreated();
        }
        if (id == -1)
            throw new RecipeNotCreated();
        for (IngredientEntry ingredient : recipe.getIngredients()) {
            try {
                addIngredient(ingredient, id);
            } catch (EntryError e) {
                break;
            }
        }

        return id;
    }

    synchronized public void updateRecipe(RecipeEntry r) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_RECIPES_NAME, r.getName());
        values.put(DBHelper.COLUMN_RECIPES_PEOPLE_NUMBER, r.getNum_people());
        values.put(DBHelper.COLUMN_RECIPES_SHAPE, ShapeType.toInteger(r.getShape()));
        if (r.isRecipeWRTPan() && r.getShape()==ShapeType.SHAPE_SQUARE)
            values.put(DBHelper.COLUMN_RECIPES_SIDE_1, r.getSideSQ());
        else
            values.put(DBHelper.COLUMN_RECIPES_SIDE_1, r.getSideL1());
        values.put(DBHelper.COLUMN_RECIPES_SIDE_2, r.getSideL2());
        values.put(DBHelper.COLUMN_RECIPES_DIAMETER, r.getDiameter());
        values.put(DBHelper.COLUMN_RECIPES_DIM_UNIT, r.getDimUnit());
        db.update(DBHelper.TABLE_RECIPES, values, DBHelper.COLUMN_RECIPES_ID + " = " + String.valueOf(r.getId()), null);

        String whereClause = DBHelper.COLUMN_INGREDIENTS_ID_RECIPE + " = " + r.getId();
        db.delete(DBHelper.TABLE_INGREDIENTS, whereClause, null);
        for (IngredientEntry ingredient : r.getIngredients()) {
            try {
                addIngredient(ingredient, r.getId());
            } catch (EntryError e) {
                break;
            }
        }
    }

    synchronized public void deleteRecipe(long id) throws EntryNotFound, EntryError {
        String whereClause = DBHelper.COLUMN_RECIPES_ID + " = " + id;
        int num = db.delete(DBHelper.TABLE_RECIPES, whereClause, null);
        if (num == 0)
            throw new EntryNotFound();
        if (num > 1)
            throw new EntryError();
        whereClause = DBHelper.COLUMN_INGREDIENTS_ID_RECIPE + " = " + id;
        db.delete(DBHelper.TABLE_INGREDIENTS, whereClause, null);
    }

    private List<IngredientEntry> getIngredients(long idRecipe) {
        List<IngredientEntry> ingredients = new ArrayList<IngredientEntry>();
        String whereClause = DBHelper.COLUMN_INGREDIENTS_ID_RECIPE + " = " + idRecipe;
        Cursor cursor = db.query(DBHelper.TABLE_INGREDIENTS, allIngredientsColumns, whereClause, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ingredients.add(cursorToIngredient(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return ingredients;
    }

    private RecipeEntry cursorToRecipe(Cursor cursor) {
        RecipeEntry recipe = new RecipeEntry();
        recipe.setId(cursor.getLong(0));
        recipe.setName(cursor.getString(1));
        recipe.setNum_people(cursor.getInt(2));
        recipe.setShape(ShapeType.fromInteger(cursor.getInt(3)));
        recipe.setSideL1(cursor.getFloat(4));
        recipe.setSideSQ(cursor.getFloat(4));
        recipe.setSideL2(cursor.getFloat(5));
        recipe.setDiameter(cursor.getFloat(6));
        recipe.setDimUnit(cursor.getInt(7));
        recipe.scaleSides();
        return recipe;
    }

    private IngredientEntry cursorToIngredient(Cursor cursor) {
        IngredientEntry ingredient = new IngredientEntry();
        ingredient.setId(cursor.getLong(0));
        ingredient.setName(cursor.getString(1));
        ingredient.setQuantity(cursor.getFloat(2));
        ingredient.setUnit(UnitType.fromInteger(cursor.getInt(3)));
        return ingredient;
    }

}
