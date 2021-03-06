package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class IngredientActivity extends AppCompatActivity {

    ArrayList<IngredientEntry> ingredientList;
    IngredientAdapter adapter;

    private RecipeEntry recipe = null;

    private RecipeEntry buildRecipe() {
        RecipeEntry r;
        long id_ = getIntent().getExtras().getLong(getString(R.string.intent_id), -1);
        if (id_ == -1) {
            r = new RecipeEntry();
            r.setId(-1);
        } else {
            try {
                RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
                recipeDAO.open();
                r = recipeDAO.getRecipe(id_);
                recipeDAO.close();
            } catch (EntryNotFound e) {
                return null;
            } catch (EntryError e) {
                return null;
            } catch (SQLException e) {
                return null;
            }
        }
        r.setName(getIntent().getExtras().getString(getString(R.string.intent_name), ""));
        r.setNum_people(getIntent().getIntExtra(getString(R.string.intent_people), -1));
        r.setShape(ShapeType.fromInteger(getIntent().getIntExtra(getString(R.string.intent_shape), ShapeType.toInteger(ShapeType.SHAPE_NOT_VALID))));
        r.setDimUnit(getIntent().getIntExtra(getString(R.string.intent_unit), 0));
        switch (r.getShape()) {
            case SHAPE_RECTANGLE:
                r.setSideL1(getIntent().getExtras().getDouble(getString(R.string.intent_side1), -1));
                r.setSideL2(getIntent().getExtras().getDouble(getString(R.string.intent_side2), -1));
                break;
            case SHAPE_SQUARE:
                r.setSideSQ(getIntent().getExtras().getDouble(getString(R.string.intent_side), -1));
                break;
            case SHAPE_CIRCLE:
                r.setDiameter(getIntent().getExtras().getDouble(getString(R.string.intent_diameter), -1));
                break;
        }
        r.scaleSides();
        return r;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        recipe = buildRecipe();

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIngredientUnit);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < UnitType.getNumber(); i++)
            list.add(UnitType.toString(UnitType.fromInteger(i)));
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner != null) {
            spinner.setAdapter(adp);
        }

        ListView lst = (ListView) findViewById(R.id.lst_ingredients);
        if (recipe.getId() == -1)
            ingredientList = new ArrayList<IngredientEntry>();
        else
            ingredientList = (ArrayList<IngredientEntry>) recipe.getIngredients();
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, ingredientList, true);
        if (lst != null) {
            lst.setAdapter(adapter);
        }

        TextView myTextView = (TextView) findViewById(R.id.txt_new_ingredients_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), getString(R.string.font_handwritten));
        if (myTextView != null) {
            myTextView.setTypeface(typeFace);
        }
    }

    private IngredientEntry checkInputs() throws WrongInputs, IngredientAlreadyPresent {
        IngredientEntry ingredient = new IngredientEntry();

        EditText editText = (EditText) findViewById(R.id.txtIngredientName);
        if (editText != null) {
            String name = editText.getText().toString();
            if (name.isEmpty())
                throw new WrongInputs();
            for (IngredientEntry entry : ingredientList)
                if (entry.getName().compareTo(name) == 0)
                    throw new IngredientAlreadyPresent();
            ingredient.setName(name);
        }

        editText = (EditText) findViewById(R.id.txtIngredientQuantity);
        if (editText != null) {
            double quantity = Double.parseDouble(editText.getText().toString());
            if (quantity <= 0.0)
                throw new WrongInputs();
            ingredient.setQuantity(quantity);
        }

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIngredientUnit);
        if (spinner != null)
            ingredient.setUnit(UnitType.fromInteger(spinner.getSelectedItemPosition()));

        return ingredient;
    }

    private void cleanInputs() {
        EditText editText = (EditText)findViewById(R.id.txtIngredientName);
        if (editText != null) {
            editText.getText().clear();
            editText.requestFocus();
        }
        editText = (EditText)findViewById(R.id.txtIngredientQuantity);
        if (editText != null)
            editText.getText().clear();
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnAddIngredient) {
            try {
                IngredientEntry ingredient = checkInputs();
                ingredientList.add(ingredient);
                adapter.notifyDataSetChanged();
                cleanInputs();
            } catch (WrongInputs e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_SHORT).show();
            } catch (IngredientAlreadyPresent e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_duplicated_ingredient), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_empty_ingredients), Toast.LENGTH_SHORT).show();
                return true;
            }
            try {
                RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
                recipeDAO.open();
                if (recipe.getId() == -1) {
                    recipe.setIngredients(ingredientList);
                    recipeDAO.addRecipe(recipe);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_recipe_added), Toast.LENGTH_SHORT).show();
                } else {
                    recipeDAO.updateRecipe(recipe);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_recipe_edited), Toast.LENGTH_SHORT).show();
                }
                recipeDAO.close();

                finish();
                startActivity(new Intent(IngredientActivity.this, RecipeActivity.class));
            } catch (SQLException e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_internal_error), Toast.LENGTH_SHORT).show();
            } catch (RecipeNotCreated e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_internal_error), Toast.LENGTH_SHORT).show();
            } catch (RecipeAlreadyPresent e) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_duplicated_recipe), Toast.LENGTH_SHORT).show();
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
