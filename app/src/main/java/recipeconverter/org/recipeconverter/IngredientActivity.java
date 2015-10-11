package recipeconverter.org.recipeconverter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import recipeconverter.org.recipeconverter.adapter.IngredientAdapter;
import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.UnitType;

public class IngredientActivity extends ActionBarActivity {

    ArrayList<IngredientEntry> ingredientList;
    IngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient);

        Spinner spinner = (Spinner) findViewById(R.id.spinnerIngredientUnit);

        ListView lst = (ListView) findViewById(R.id.lst_ingredients);
        ingredientList = new ArrayList<>();
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, ingredientList);
        lst.setAdapter(adapter);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddIngredient:
                EditText editText = (EditText) findViewById(R.id.txtIngredientName);
                IngredientEntry ingredient = new IngredientEntry();
                ingredient.setName(editText.getText().toString());
                editText = (EditText) findViewById(R.id.txtIngredientQuantity);
                ingredient.setQuantity(Double.parseDouble(editText.getText().toString()));
                Spinner spinner = (Spinner) findViewById(R.id.spinnerIngredientUnit);
                ingredient.setUnit(UnitType.fromInteger(spinner.getSelectedItemPosition()));
                ingredientList.add(ingredient);
                adapter.notifyDataSetChanged();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
