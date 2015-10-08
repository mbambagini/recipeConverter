package recipeconverter.org.recipeconverter.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * class to handle DB creation/update
 * 
 * @author Mario Bambagini
 *
 */
public class DBHelper extends SQLiteOpenHelper {

	//databases
	public static final String TABLE_RECIPES = "RECIPES";
	public static final String TABLE_INGREDIENTS = "INGREDIENTS";
	//columns
    public static final String COLUMN_RECIPES_ID = "_id";
	public static final String COLUMN_RECIPES_NAME = "name";
	public static final String COLUMN_RECIPES_PEOPLE_NUMBER = "people_number";
	public static final String COLUMN_RECIPES_SHAPE = "shape";
	public static final String COLUMN_RECIPES_SIDE_1 = "side_1";
	public static final String COLUMN_RECIPES_SIDE_2 = "side_2";
	public static final String COLUMN_RECIPES_DIAMETER = "diameter";
	public static final String COLUMN_RECIPES_TYPE = "type";
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
            + COLUMN_RECIPES_TYPE + " integer);";
    public static final String COLUMN_INGREDIENTS_ID = "_id";
	public static final String COLUMN_INGREDIENTS_NAME = "name";
	public static final String COLUMN_INGREDIENTS_QUANTITY = "quantity";
	public static final String COLUMN_INGREDIENTS_UNIT = "unit";
    public static final String COLUMN_INGREDIENTS_ID_RECIPE = "idRecipe";
    private static final String DATABASE_CREATE_INGREDIENTS = "create table "
            + TABLE_INGREDIENTS + "("
            + COLUMN_INGREDIENTS_ID + " integer primary key autoincrement, "
            + COLUMN_INGREDIENTS_NAME + " text not null unique,"
            + COLUMN_INGREDIENTS_QUANTITY + " float, "
            + COLUMN_INGREDIENTS_UNIT + " integer, "
            + COLUMN_INGREDIENTS_ID_RECIPE + " integer);";
    //extra
	public static final int DB_TRUE = 1;
	public static final int DB_FALSE = 0;

    public static final int DB_SHAPE_CIRCLE = 0;
    public static final int DB_SHAPE_RECTANGLE = 1;
    public static final int DB_SHAPE_SQUARE = 2;

    /*
        public static final int DB_TYPE_ENTRY = 0;
        public static final int DB_TYPE_FIRST = 1;
        public static final int DB_TYPE_SECOND = 2;
        public static final int DB_TYPE_SIDE = 3;
        public static final int DB_TYPE_CAKE = 4;
    */
    //version
    private static final int DATABASE_VERSION = 1;
    //file
    private static final String DATABASE_NAME = "recipes.db";

    public DBHelper(Context context) {
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
