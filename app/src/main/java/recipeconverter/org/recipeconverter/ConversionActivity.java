package recipeconverter.org.recipeconverter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;

import recipeconverter.org.recipeconverter.adapter.IngredientAdapter;
import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.RecipeDAO;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;
import recipeconverter.org.recipeconverter.exception.EntryError;
import recipeconverter.org.recipeconverter.exception.EntryNotFound;


public class ConversionActivity extends ActionBarActivity {

    IngredientAdapter adapter;
    private long id = -1;
    private ArrayList<IngredientEntry> ingredients = null;
    private ArrayList<IngredientEntry> ingredients_original = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        id = getIntent().getExtras().getLong("id", -1);

        RecipeEntry recipe = null;
        try {
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            recipe = recipeDAO.getRecipe(id);
            if (recipe == null) return;
            ingredients = (ArrayList<IngredientEntry>) recipe.getIngredients();
            if (ingredients == null) return;
            ingredients_original = (ArrayList<IngredientEntry>) ingredients.clone();
            if (ingredients_original == null) return;
            recipeDAO.close();
        } catch (SQLException e) {
        } catch (EntryNotFound e) {
        } catch (EntryError e) {
        }

        ListView lst = (ListView) findViewById(R.id.lst_converted_ingredients);
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, ingredients);
        lst.setAdapter(adapter);

        if (recipe.getNum_people() > -1) {
            findViewById(R.id.layoutConvertedHowManyPeople).setVisibility(View.VISIBLE);
        } else
            findViewById(R.id.layoutConvertedHowManyPeople).setVisibility(View.GONE);

        findViewById(R.id.layoutConvertedShapeCircle).setVisibility(View.GONE);
        findViewById(R.id.layoutConvertedShapeRect).setVisibility(View.GONE);
        findViewById(R.id.layoutConvertedShapeSquare).setVisibility(View.GONE);
        switch (recipe.getShape()) {
            case SHAPE_CIRCLE:
                findViewById(R.id.layoutConvertedShapeCircle).setVisibility(View.VISIBLE);
                break;
            case SHAPE_RECTANGLE:
                findViewById(R.id.layoutConvertedShapeRect).setVisibility(View.VISIBLE);
                break;
            case SHAPE_SQUARE:
                findViewById(R.id.layoutConvertedShapeSquare).setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_conversion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
