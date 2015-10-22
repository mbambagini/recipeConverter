package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DecimalFormat;
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
import recipeconverter.org.recipeconverter.exception.WrongInputs;

public class ConversionActivity extends ActionBarActivity {

    private IngredientAdapter adapter;

    private int shapeSelection = 0;

    private RecipeEntry recipe_orig = null;
    private RecipeEntry recipe_conv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        long id = getIntent().getExtras().getLong("id", -1);
        if (id == -1 || !loadRecipe(id)) {
            Toast.makeText(getApplicationContext(),
                           getResources().getString(R.string.toast_internal_error),
                           Toast.LENGTH_LONG).show();
//          Intent intent = new Intent(ConversionActivity.this, RecipeActivity.class);
//          startActivity(intent);
            finish();
        }

        fillUnitSpinner();
        fillShapeSpinner();
        showFields();

        ListView lst = (ListView) findViewById(R.id.lst_converted_ingredients);
        adapter = new IngredientAdapter(this, android.R.layout.simple_list_item_1, recipe_conv.getIngredients(), false);
        lst.setAdapter(adapter);

        TextView myTextView = (TextView) findViewById(R.id.txt_conversion_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/JennaSue.ttf");
        myTextView.setTypeface(typeFace);
    }

    private boolean loadRecipe (long id) {
        try {
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            recipe_orig = recipeDAO.getRecipe(id);
            recipeDAO.close();
            if (recipe_orig == null)
                return false;
            recipe_conv = recipe_orig.clone();
        } catch (EntryNotFound | EntryError | SQLException e) {
            return false;
        }
        return true;
    }

    private void showFields() {
        EditText txt;

        if (recipe_orig.isRecipeWRTPeople()) {
            //set the field as visible
            findViewById(R.id.layoutConvertedHowManyPeople).setVisibility(View.VISIBLE);
            //set field initial value
            txt = (EditText)findViewById(R.id.txtConvertedRecipePeople);
            txt.setText(""+recipe_orig.getNum_people(), TextView.BufferType.EDITABLE);
        } else {
            //set the field as not visible
            findViewById(R.id.layoutConvertedHowManyPeople).setVisibility(View.GONE);            
        }

        findViewById(R.id.layoutConvertedShapeCircle).setVisibility(View.GONE);
        findViewById(R.id.layoutConvertedShapeRect).setVisibility(View.GONE);
        findViewById(R.id.layoutConvertedShapeSquare).setVisibility(View.GONE);
        if (recipe_orig.isRecipeWRTPan()) {
            //set the fields as visible and their initial values
            findViewById(R.id.layoutConversionPan).setVisibility(View.VISIBLE);
            findViewById(R.id.layoutConversionShapeDimUnit).setVisibility(View.VISIBLE);
            switch (recipe_orig.getShape()) {
                case SHAPE_CIRCLE:
                    findViewById(R.id.layoutConvertedShapeCircle).setVisibility(View.VISIBLE);
                    txt = (EditText)findViewById(R.id.txtConvertedRecipeDiameter);
                    txt.setText(""+recipe_orig.getDiameter(), TextView.BufferType.EDITABLE);
                    break;
                case SHAPE_RECTANGLE:
                    findViewById(R.id.layoutConvertedShapeRect).setVisibility(View.VISIBLE);
                    txt = (EditText)findViewById(R.id.txtConvertedRecipeSide1);
                    txt.setText(""+recipe_orig.getSide1(), TextView.BufferType.EDITABLE);
                    txt = (EditText)findViewById(R.id.txtConvertedRecipeSide2);
                    txt.setText(""+recipe_orig.getSide2(), TextView.BufferType.EDITABLE);
                    break;
                case SHAPE_SQUARE:
                    findViewById(R.id.layoutConvertedShapeSquare).setVisibility(View.VISIBLE);
                    txt = (EditText)findViewById(R.id.txtConvertedRecipeSide);
                    txt.setText(""+recipe_orig.getSide1(), TextView.BufferType.EDITABLE);
                    break;
            }
        } else {
            //set the fields as not visible
            findViewById(R.id.layoutConversionShapeDimUnit).setVisibility(View.GONE);
            findViewById(R.id.layoutConversionPan).setVisibility(View.GONE);
        }
    }

    private void fillUnitSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spnConversionUnit);
        List<String> list = new ArrayList<>();
        list.add("Centimeter");
        list.add("Inch");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(recipe_orig.getDimUnit());
    }

    private void fillShapeSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spnConversionPanShape);
        List<String> list = new ArrayList<>();
        list.add("Rectangle");
        list.add("Square");
        list.add("Circle");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                updatePanShapeFields(pos);
                shapeSelection = pos;
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spinner.setSelection(ShapeType.toInteger(recipe_orig.getShape()));
        shapeSelection = ShapeType.toInteger(recipe_orig.getShape());
    }

    private void updatePanShapeFields(int selection) {
        findViewById(R.id.layoutConvertedShapeRect).setVisibility((selection == 0) ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutConvertedShapeSquare).setVisibility((selection == 1) ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutConvertedShapeCircle).setVisibility((selection == 2) ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                Toast.makeText(this,
                               getResources().getString(R.string.toast_no_sharing),
                               Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private String buildString() {
        //error detection
        if (recipe_conv == null || recipe_conv.getIngredients().size() == 0)
            return null;

        DecimalFormat format = new DecimalFormat("0.00");
        //header
        String buffer = "Recipe \"" + recipe_conv.getName() + "\"";
        if (recipe_conv.isRecipeWRTPeople())
            buffer += " for " + recipe_conv.getNum_people() + " people\n";
        if (recipe_conv.isRecipeWRTPan()) {
            switch(recipe_conv.getShape()) {
            case SHAPE_CIRCLE:
                buffer += " for circular pan with diameter: " +
                          format.format(recipe_conv.getDiameter());
                break;
            case SHAPE_RECTANGLE:
                buffer += " for rectangular pan with size: " +
                          format.format(recipe_conv.getSide1()) + " x " +
                          format.format(recipe_conv.getSide2());
                break;
            case SHAPE_SQUARE:
                buffer += " for squared pan: " +
                          format.format(recipe_conv.getSide1()) + " x " +
                          format.format(recipe_conv.getSide1());
                break;
            }
            buffer += " " + ((recipe_conv.getDimUnit() == 0) ? "cm" : "inch") + "\n";
        }
        //body
        for (IngredientEntry ingredient : recipe_conv.getIngredients())
            buffer += format.format(ingredient.getQuantity()) + " " +
                      UnitType.toString(ingredient.getUnit()) + " - " +
                      ingredient.getName() + "\n";
        return buffer;
    }

    private void updateRecipe () throws WrongInputs {
        if (recipe_orig.isRecipeWRTPeople()) {
            int new_people = Integer.parseInt(((EditText)findViewById(R.id.txtConvertedRecipePeople)).getText().toString());
            if (new_people > 0)
                recipe_conv.setNum_people(new_people);
            else
                throw new WrongInputs();
        }
        if (recipe_orig.isRecipeWRTPan()) {
            recipe_conv.setDimUnit(((Spinner) findViewById(R.id.spnConversionUnit)).getSelectedItemPosition());
            recipe_conv.setShape(ShapeType.fromInteger(shapeSelection));
            switch (recipe_conv.getShape()) {
            case SHAPE_CIRCLE:
                double new_diameter = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeDiameter)).getText().toString());
                if (new_diameter > 0.0)
                    recipe_conv.setDiameter(new_diameter);
                else
                    throw new WrongInputs();
                break;
            case SHAPE_RECTANGLE:
                double new_side1 = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeSide1)).getText().toString());
                double new_side2 = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeSide2)).getText().toString());
                if (new_side1 > 0.0 && new_side2 > 0.0) {
                    recipe_conv.setSide1(new_side1);
                    recipe_conv.setSide2(new_side2);
                } else
                    throw new WrongInputs();
                break;
            case SHAPE_SQUARE:
                double new_side = Double.parseDouble(((EditText)findViewById(R.id.txtConvertedRecipeSide)).getText().toString());
                if (new_side > 0.0)
                    recipe_conv.setSide1(new_side);
                else
                    throw new WrongInputs();
                break;
            }
        }

        double factor = 1.0;
        if (recipe_orig.isRecipeWRTPeople()) {
            factor = (double) recipe_conv.getNum_people() / recipe_orig.getNum_people();
        }
        if (recipe_orig.isRecipeWRTPan()) {
            factor = recipe_conv.getSurfaceCM2() / recipe_orig.getSurfaceCM2();
        }
        recipe_conv.getIngredients().clear();
        for (IngredientEntry ingredient : recipe_orig.getIngredients()) {
            IngredientEntry tmp = ingredient.clone();
            tmp.setQuantity(tmp.getQuantity() * factor);
            recipe_conv.getIngredients().add(tmp);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnConvert) {
            try {
                updateRecipe();
                adapter.notifyDataSetChanged();
            } catch (NullPointerException | NumberFormatException | WrongInputs e) {
                Toast.makeText(this,
                               getResources().getString(R.string.toast_wrong_input),
                               Toast.LENGTH_LONG).show();
            }
        }
    }

}
