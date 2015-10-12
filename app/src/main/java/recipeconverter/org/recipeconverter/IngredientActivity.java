package recipeconverter.org.recipeconverter;

import recipeconverter.org.recipeconverter.exception.WrongInputs;
import recipeconverter.org.recipeconverter.exception.IngredientAlreadyPresent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import recipeconverter.org.recipeconverter.adapter.IngredientAdapter;
import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.UnitType;

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
        list.add("ounce");
        list.add("pound");
        list.add("gram");
        list.add("kilogram");
        list.add("gallon");
        list.add("litre");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ListView lst = (ListView) findViewById(R.id.lst_ingredients);
        ingredientList = new ArrayList<>();
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, ingredientList);
        lst.setAdapter(adapter);
    }

    private IngredientEntry checkInputs () throws WrongInputs, IngredientAlreadyPresent {
        IngredientEntry ingredient = new IngredientEntry();

        String name = ((EditText)findViewById(R.id.txtIngredientName)).getText().toString();
        if (name.compareTo("") == 0)
            throw new WrongInputs();
        for (IngredientEntry entry: ingredientList)
            if (entry.getName().compareTo(name) == 0)
                throw new IngredientAlreadyPresent();
        ingredient.setName(editText);

        double quantity = Double.parseDouble(((EditText)findViewById(R.id.txtIngredientQuantity)).getText().toString());
        if (quantity <= 0.0)
            throw new WrongInputs();
        ingredient.setQuantity(quantity);

        ingredient.setUnit(UnitType.fromInteger(((Spinner)findViewById(R.id.spinnerIngredientUnit)).getSelectedItemPosition() + 1));
    }
    
    private void cleanInptuts () {
        ((EditText)findViewById(R.id.txtIngredientName)).getText().clear();
        ((EditText)findViewById(R.id.txtIngredientQuantity)).getText().clear();
    }

    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnAddIngredient:
            try {
                IngredientEntry ingredient = checkInputs();
                ingredientList.add(ingredient);
                adapter.notifyDataSetChanged();
                cleanInptuts();
            } catch (WrongInputs e) {
                Toast.makeText(getApplicationContext(), "Set all inputs correctly", Toast.LENGTH_SHORT).show();
                return;
            } catch (IngredientAlreadyPresent e) {
                Toast.makeText(getApplicationContext(), "Ingredient already present", Toast.LENGTH_SHORT).show();
                return;
            }
            break;
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (id == -1) {
                Toast.makeText(getApplicationContext(), "Internal error", Toast.LENGTH_SHORT).show();
                return true;
            }
            if (ingredientList != null && ingredientList.size() == 0) {
                Toast.makeText(getApplicationContext(), "Insert at least one ingredient", Toast.LENGTH_SHORT).show();
                return true;
            }
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            for (IngredientEntry entry: ingredientList)
                recipeDAO.addIngredient(entry, id);
            recipeDAO.close();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
