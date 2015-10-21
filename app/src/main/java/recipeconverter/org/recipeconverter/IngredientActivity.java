package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import recipeconverter.org.recipeconverter.adapter.IngredientAdapter;
import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.RecipeDAO;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;
import recipeconverter.org.recipeconverter.dao.ShapeType;
import recipeconverter.org.recipeconverter.dao.UnitType;
import recipeconverter.org.recipeconverter.exception.EntryError;
import recipeconverter.org.recipeconverter.exception.EntryNotFound;
import recipeconverter.org.recipeconverter.exception.IngredientAlreadyPresent;
import recipeconverter.org.recipeconverter.exception.RecipeAlreadyPresent;
import recipeconverter.org.recipeconverter.exception.RecipeNotCreated;
import recipeconverter.org.recipeconverter.exception.WrongInputs;

public class IngredientActivity extends ActionBarActivity {

    ArrayList<IngredientEntry> ingredientList;
    IngredientAdapter adapter;

    private RecipeEntry recipe = null;

    private RecipeEntry buildRecipe() {
        RecipeEntry r;
        long id_ = getIntent().getExtras().getLong("id", -1);
        if (id_ == -1) {
            r = new RecipeEntry();
            r.setId(-1);
        } else {
            try {
                RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
                recipeDAO.open();
                r = recipeDAO.getRecipe(id_);
                recipeDAO.close();
            } catch (EntryNotFound | EntryError | SQLException e) {
                return null;
            }
        }
        Toast.makeText(getApplicationContext(), "" + r.getId(), Toast.LENGTH_LONG).show();
        r.setName(getIntent().getExtras().getString("name", ""));
        r.setNum_people(getIntent().getIntExtra("num_people", -1));
        r.setShape(ShapeType.fromInteger(getIntent().getIntExtra("shape", ShapeType.toInteger(ShapeType.SHAPE_NOT_VALID))));
        r.setDimUnit(getIntent().getIntExtra("unit", 0));
        switch (r.getShape()) {
            case SHAPE_RECTANGLE:
                r.setSide1(getIntent().getExtras().getDouble("side1", -1));
                r.setSide2(getIntent().getExtras().getDouble("side2", -1));
                break;
            case SHAPE_SQUARE:
                r.setSide1(getIntent().getExtras().getDouble("side", -1));
                break;
            case SHAPE_CIRCLE:
                r.setDiameter(getIntent().getExtras().getDouble("diameter", -1));
                break;
        }
        return r;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        recipe = buildRecipe();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIngredientUnit);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < UnitType.getNumber(); i++)
            list.add(UnitType.toString(UnitType.fromInteger(i)));
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp);

        ListView lst = (ListView) findViewById(R.id.lst_ingredients);
        if (recipe.getId() == -1)
            ingredientList = new ArrayList<>();
        else
            ingredientList = (ArrayList<IngredientEntry>) recipe.getIngredients();
        Toast.makeText(getApplicationContext(), "NUM: " + recipe.getIngredients().size(), Toast.LENGTH_LONG).show();
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, ingredientList, true);
        lst.setAdapter(adapter);

        TextView myTextView = (TextView) findViewById(R.id.txt_new_ingredients_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/JennaSue.ttf");
        myTextView.setTypeface(typeFace);
    }

    private IngredientEntry checkInputs() throws WrongInputs, IngredientAlreadyPresent {
        IngredientEntry ingredient = new IngredientEntry();

        String name = ((EditText) findViewById(R.id.txtIngredientName)).getText().toString();
        if (name.compareTo("") == 0)
            throw new WrongInputs();
        for (IngredientEntry entry : ingredientList)
            if (entry.getName().compareTo(name) == 0)
                throw new IngredientAlreadyPresent();
        ingredient.setName(name);

        double quantity = Double.parseDouble(((EditText) findViewById(R.id.txtIngredientQuantity)).getText().toString());
        if (quantity <= 0.0)
            throw new WrongInputs();
        ingredient.setQuantity(quantity);

        ingredient.setUnit(UnitType.fromInteger(((Spinner) findViewById(R.id.spinnerIngredientUnit)).getSelectedItemPosition()));

        return ingredient;
    }

    private void cleanInptuts() {
        ((EditText) findViewById(R.id.txtIngredientName)).getText().clear();
        ((EditText) findViewById(R.id.txtIngredientQuantity)).getText().clear();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnAddIngredient) {
            try {
                IngredientEntry ingredient = checkInputs();
                ingredientList.add(ingredient);
                adapter.notifyDataSetChanged();
                cleanInptuts();
            } catch (WrongInputs | NullPointerException | NumberFormatException e) {
                Toast.makeText(getApplicationContext(),
                               getResources().getString(R.string.toast_wrong_input),
                               Toast.LENGTH_SHORT).show();
            } catch (IngredientAlreadyPresent e) {
                Toast.makeText(getApplicationContext(),
                               getResources().getString(R.string.toast_duplicated_ingredient),
                               Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ingredient, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_ingredient_save) {
            if (ingredientList != null && ingredientList.size() == 0) {
                Toast.makeText(getApplicationContext(),
                               getResources().getString(R.string.toast_empty_ingredients),
                               Toast.LENGTH_SHORT).show();
                return true;
            }
            try {
                RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
                recipeDAO.open();
                if (recipe.getId() == -1) {
                    recipe.setIngredients(ingredientList);
                    recipeDAO.addRecipe(recipe);
                } else
                    recipeDAO.updateRecipe(recipe);
                recipeDAO.close();
                Toast.makeText(getApplicationContext(),
                               getResources().getString(R.string.toast_recipe_added),
                               Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(IngredientActivity.this, RecipeActivity.class));
            } catch (SQLException | RecipeNotCreated e) {
                Toast.makeText(getApplicationContext(),
                               getResources().getString(R.string.toast_internal_error),
                               Toast.LENGTH_SHORT).show();
            } catch (RecipeAlreadyPresent e) {
                Toast.makeText(getApplicationContext(),
                               getResources().getString(R.string.toast_duplicated_recipe),
                               Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        if (item.getItemId() == R.id.action_ingredient_discard) {
            finish();
            startActivity(new Intent(IngredientActivity.this, RecipeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
