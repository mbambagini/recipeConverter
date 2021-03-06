package recipeconverter.org.recipeconverter.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * class to handle DB creation/update
 *
 * @author Mario Bambagini
 */
class DBHelper extends SQLiteOpenHelper {

    //databases
    static final String TABLE_RECIPES = "RECIPES";
    static final String TABLE_INGREDIENTS = "INGREDIENTS";
    //columns
    static final String COLUMN_RECIPES_ID = "_id";
    static final String COLUMN_RECIPES_NAME = "name";
    static final String COLUMN_RECIPES_PEOPLE_NUMBER = "people_number";
    static final String COLUMN_RECIPES_SHAPE = "shape";
    static final String COLUMN_RECIPES_SIDE_1 = "side_1";
    static final String COLUMN_RECIPES_SIDE_2 = "side_2";
    static final String COLUMN_RECIPES_DIAMETER = "diameter";
    static final String COLUMN_RECIPES_DIM_UNIT = "dim_unit";
    //creation queries
    private static final String DATABASE_CREATE_RECIPES = "create table "
            + TABLE_RECIPES + "("
            + COLUMN_RECIPES_ID + " integer primary key autoincrement, "
            + COLUMN_RECIPES_NAME + " text not null unique,"
            + COLUMN_RECIPES_PEOPLE_NUMBER + " integer,"
            + COLUMN_RECIPES_SHAPE + " integer,"
            + COLUMN_RECIPES_SIDE_1 + " float,"
            + COLUMN_RECIPES_SIDE_2 + " float,"
            + COLUMN_RECIPES_DIAMETER + " float,"
            + COLUMN_RECIPES_DIM_UNIT + " integer);";
    static final String COLUMN_INGREDIENTS_ID = "_id";
    static final String COLUMN_INGREDIENTS_NAME = "name";
    static final String COLUMN_INGREDIENTS_QUANTITY = "quantity";
    static final String COLUMN_INGREDIENTS_UNIT = "unit";
    static final String COLUMN_INGREDIENTS_ID_RECIPE = "idRecipe";
    private static final String DATABASE_CREATE_INGREDIENTS = "create table "
            + TABLE_INGREDIENTS + "("
            + COLUMN_INGREDIENTS_ID + " integer primary key autoincrement, "
            + COLUMN_INGREDIENTS_NAME + " text not null,"
            + COLUMN_INGREDIENTS_QUANTITY + " float, "
            + COLUMN_INGREDIENTS_UNIT + " integer, "
            + COLUMN_INGREDIENTS_ID_RECIPE + " integer);";

    //version
    private static final int DATABASE_VERSION = 3;
    //file
    private static final String DATABASE_NAME = "recipes.db";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_RECIPES);
        database.execSQL(DATABASE_CREATE_INGREDIENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }
}
