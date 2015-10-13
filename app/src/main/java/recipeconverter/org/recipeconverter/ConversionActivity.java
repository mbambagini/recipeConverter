package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import recipeconverter.org.recipeconverter.adapter.IngredientAdapter;
import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.RecipeDAO;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;
import recipeconverter.org.recipeconverter.dao.UnitType;
import recipeconverter.org.recipeconverter.exception.EntryError;
import recipeconverter.org.recipeconverter.exception.EntryNotFound;

public class ConversionActivity extends ActionBarActivity {

    static final double pi_ = 3.14;
    IngredientAdapter adapter;
    private int original_people = -1;
    private double original_area = -1.0;
    private RecipeEntry recipe = null;
    private ArrayList<IngredientEntry> ingredients = null;
    private ArrayList<IngredientEntry> ingredients_original = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        long id = getIntent().getExtras().getLong("id", -1);
        if (id == -1)
            return;

        loadIngredients(id);

        showFields();

        ListView lst = (ListView) findViewById(R.id.lst_converted_ingredients);
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, ingredients);
        lst.setAdapter(adapter);

        TextView myTextView = (TextView) findViewById(R.id.txt_conversion_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/JennaSue.ttf");
        myTextView.setTypeface(typeFace);
    }

    private void showFields() {
        if (recipe.getNum_people() > -1) {
            findViewById(R.id.layoutConvertedHowManyPeople).setVisibility(View.VISIBLE);
            EditText txt = (EditText)findViewById(R.id.txtConvertedRecipePeople);
            txt.setText(""+recipe.getNum_people(), TextView.BufferType.EDITABLE);
            original_people = recipe.getNum_people();
        } else
            findViewById(R.id.layoutConvertedHowManyPeople).setVisibility(View.GONE);

        findViewById(R.id.layoutConvertedShapeCircle).setVisibility(View.GONE);
        findViewById(R.id.layoutConvertedShapeRect).setVisibility(View.GONE);
        findViewById(R.id.layoutConvertedShapeSquare).setVisibility(View.GONE);

        EditText txt;
        switch (recipe.getShape()) {
            case SHAPE_CIRCLE:
                findViewById(R.id.layoutConvertedShapeCircle).setVisibility(View.VISIBLE);
                txt = (EditText)findViewById(R.id.txtConvertedRecipeDiameter);
                txt.setText(""+recipe.getDiameter(), TextView.BufferType.EDITABLE);
                original_area = recipe.getDiameter() * pi_;
                break;
            case SHAPE_RECTANGLE:
                findViewById(R.id.layoutConvertedShapeRect).setVisibility(View.VISIBLE);
                txt = (EditText)findViewById(R.id.txtConvertedRecipeSide1);
                txt.setText(""+recipe.getSide1(), TextView.BufferType.EDITABLE);
                txt = (EditText)findViewById(R.id.txtConvertedRecipeSide2);
                txt.setText(""+recipe.getSide2(), TextView.BufferType.EDITABLE);
                original_area = recipe.getSide1() * recipe.getSide2();
                break;
            case SHAPE_SQUARE:
                findViewById(R.id.layoutConvertedShapeSquare).setVisibility(View.VISIBLE);
                txt = (EditText)findViewById(R.id.txtConvertedRecipeSide);
                txt.setText(""+recipe.getSide1(), TextView.BufferType.EDITABLE);
                original_area = recipe.getSide1() * recipe.getSide1();
                break;
        }
    }

    private void loadIngredients(long id) {
        try {
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            recipe = recipeDAO.getRecipe(id);
            if (recipe == null)
                return;
            ingredients_original = (ArrayList<IngredientEntry>) recipe.getIngredients();
            ingredients = new ArrayList<>();
            cloneIngredients(1.0);
            recipeDAO.close();
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "DB error", Toast.LENGTH_LONG).show();
        } catch (EntryNotFound | EntryError e) {
            Toast.makeText(getApplicationContext(), "internal error", Toast.LENGTH_LONG).show();
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
        if (item.getItemId() == R.id.action_conversion_share) {
            String msg = buildString();
            if (msg != null) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "share"));
            } else {
                Toast.makeText(this, "impossible to share", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    
    private String buildString() {
        if (ingredients == null || ingredients.size() == 0)
            return null;
        String buffer = "";
        for (IngredientEntry ingredient : ingredients)
            buffer += ingredient.getQuantity() + " " +
                    UnitType.toInteger(ingredient.getUnit()) + " - " +
                    ingredient.getName();
        return buffer;
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnConvert) {
            double factor = 1.0;
            if (original_people > 0) {
                int new_people = Integer.parseInt(((EditText)findViewById(R.id.txtConvertedRecipePeople)).getText().toString());
                if (new_people > 0)
                    factor = (double) new_people / original_people;
            }
            switch (recipe.getShape()) {
            case SHAPE_CIRCLE:
                double diameter = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeDiameter)).getText().toString());
                if (diameter > 0)
                    factor = (diameter * pi_) / original_area;
                break;
            case SHAPE_RECTANGLE:
                double side1 = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeSide1)).getText().toString());
                double side2 = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeSide2)).getText().toString());
                if (side1 > 0 && side2 > 0)
                    factor = (side1 * side2) / original_area;
                break;
            case SHAPE_SQUARE:
                double side = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeSide)).getText().toString());
                if (side > 0)
                    factor = (side * side) / original_area;
                break;
            }
            convertIngredients(factor);
        }
    }

    private void cloneIngredients(double factor) {
        for (IngredientEntry ingredient : ingredients_original) {
            IngredientEntry tmp = ingredient.clone();
            tmp.setQuantity(tmp.getQuantity() * factor);
            ingredients.add(tmp);
        }
    }

    private void convertIngredients(double factor) {
        ingredients.clear();
        cloneIngredients(factor);
        adapter.notifyDataSetChanged();
    }

}
