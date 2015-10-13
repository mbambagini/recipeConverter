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
import recipeconverter.org.recipeconverter.dao.UnitType;
import recipeconverter.org.recipeconverter.exception.EntryError;
import recipeconverter.org.recipeconverter.exception.IngredientAlreadyPresent;
import recipeconverter.org.recipeconverter.exception.WrongInputs;

public class IngredientActivity extends ActionBarActivity {

    ArrayList<IngredientEntry> ingredientList;
    IngredientAdapter adapter;

    private long id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        id = getIntent().getExtras().getLong("id", -1);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIngredientUnit);
        List<String> list = new ArrayList<>();
        list.add(UnitType.toString(UnitType.UNIT_OUNCE));
        list.add(UnitType.toString(UnitType.UNIT_POUND));
        list.add(UnitType.toString(UnitType.UNIT_GRAM));
        list.add(UnitType.toString(UnitType.UNIT_KILOGRAM));
        list.add(UnitType.toString(UnitType.UNIT_GALLON));
        list.add(UnitType.toString(UnitType.UNIT_LITRE));
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adp);

        ListView lst = (ListView) findViewById(R.id.lst_ingredients);
        ingredientList = new ArrayList<>();
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, ingredientList);
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

        ingredient.setUnit(UnitType.fromInteger(((Spinner) findViewById(R.id.spinnerIngredientUnit)).getSelectedItemPosition() + 1));

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
            } catch (WrongInputs e) {
                Toast.makeText(getApplicationContext(), "Set all inputs correctly", Toast.LENGTH_SHORT).show();
            } catch (IngredientAlreadyPresent e) {
                Toast.makeText(getApplicationContext(), "Ingredient already present", Toast.LENGTH_SHORT).show();
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
        if (item.getItemId() == R.id.action_settings) {
            if (id == -1) {
                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (ingredientList != null && ingredientList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Insert at least one ingredient", Toast.LENGTH_SHORT).show();
                return true;
            }
            try {
                RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
                recipeDAO.open();
                for (IngredientEntry entry : ingredientList)
                    recipeDAO.addIngredient(entry, id);
                recipeDAO.close();
                Intent intent = new Intent(IngredientActivity.this, ConversionActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            } catch (SQLException | EntryError e) {
                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_SHORT).show();
                return true;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
