package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ConversionActivity extends AppCompatActivity {

    private IngredientAdapter adapter;

    private int shapeSelection = 0;

    private RecipeEntry recipe_orig = null;
    private RecipeEntry recipe_conv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversion);

        long id = getIntent().getExtras().getLong(getString(R.string.intent_id), -1);
        if (id == -1 || !loadRecipe(id)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_internal_error), Toast.LENGTH_LONG).show();
            finish();
        }

        fillUnitSpinner();
        fillShapeSpinner();
        showFields();

        ListView lst = (ListView) findViewById(R.id.lst_converted_ingredients);
        adapter = new IngredientAdapter(this,
                                        android.R.layout.simple_list_item_1,
                                        recipe_conv.getIngredients(), false);
        if (lst != null)
            lst.setAdapter(adapter);

        TextView myTextView = (TextView) findViewById(R.id.txt_conversion_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), getString(R.string.font_handwritten));
        if (myTextView != null)
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
        } catch (EntryNotFound e) {
            return false;
        } catch (EntryError e) {
            return false;
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    private void showFields() {
        if (recipe_orig.isRecipeWRTPeople()) {
            //set the field as visible
            View v = findViewById(R.id.layoutConvertedHowManyPeople);
            if (v != null)
                v.setVisibility(View.VISIBLE);
            //set field initial value
            EditText txt = (EditText)findViewById(R.id.txtConvertedRecipePeople);
            if (txt != null)
                txt.setText(String.valueOf(recipe_orig.getNum_people()), TextView.BufferType.EDITABLE);

            // set pan fields not visible
             v = findViewById(R.id.layoutConvertedShapeCircle);
            if (v != null)
                v.setVisibility(View.GONE);
            v = findViewById(R.id.layoutConvertedShapeCircle);
            if (v != null)
                v.setVisibility(View.GONE);
            v = findViewById(R.id.layoutConvertedShapeRect);
            if (v != null)
                v.setVisibility(View.GONE);
            v = findViewById(R.id.layoutConvertedShapeSquare);
            if (v != null)
                v.setVisibility(View.GONE);
            v = findViewById(R.id.layoutConversionShapeDimUnit);
            if (v != null)
                v.setVisibility(View.GONE);
            v = findViewById(R.id.layoutConversionPan);
            if (v != null)
                v.setVisibility(View.GONE);
        } else {
            // it is pan

            //set the people field as not visible
            View v = findViewById(R.id.layoutConvertedHowManyPeople);
            if (v != null)
                v.setVisibility(View.GONE);

            //set the fields as visible and their initial values
            v = findViewById(R.id.layoutConversionPan);
            if (v != null)
                v.setVisibility(View.VISIBLE);
            v = findViewById(R.id.layoutConversionShapeDimUnit);
            if (v != null)
                v.setVisibility(View.VISIBLE);

            EditText txt_diameter = (EditText)findViewById(R.id.txtConvertedRecipeDiameter);
            EditText txt_side1 = (EditText)findViewById(R.id.txtConvertedRecipeSide1);
            EditText txt_side2 = (EditText)findViewById(R.id.txtConvertedRecipeSide2);
            EditText txt_side = (EditText)findViewById(R.id.txtConvertedRecipeSide);
            DecimalFormat format = new DecimalFormat(getString(R.string.double_formatting));
            if (txt_diameter != null)
                txt_diameter.setText(format.format(recipe_orig.getDiameter()).replace(',', '.'), TextView.BufferType.EDITABLE);
            if (txt_side != null)
                txt_side.setText(format.format(recipe_orig.getSideSQ()).replace(',', '.'), TextView.BufferType.EDITABLE);
            if (txt_side1 != null)
                txt_side1.setText(format.format(recipe_orig.getSideL1()).replace(',', '.'), TextView.BufferType.EDITABLE);
            if (txt_side2 != null)
                txt_side2.setText(format.format(recipe_orig.getSideL2()).replace(',', '.'), TextView.BufferType.EDITABLE);
            v = findViewById(R.id.layoutConvertedShapeCircle);
            if (v != null)
                v.setVisibility(recipe_orig.getShape()==ShapeType.SHAPE_CIRCLE ? View.VISIBLE : View.GONE);
            v = findViewById(R.id.layoutConvertedShapeRect);
            if (v != null)
                v.setVisibility(recipe_orig.getShape()==ShapeType.SHAPE_RECTANGLE ? View.VISIBLE : View.GONE);
            v = findViewById(R.id.layoutConvertedShapeSquare);
            if (v != null)
                v.setVisibility(recipe_orig.getShape()==ShapeType.SHAPE_SQUARE ? View.VISIBLE : View.GONE);
        }
    }

    private void fillUnitSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spnConversionUnit);
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.str_cm));
        list.add(getString(R.string.str_in));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
            spinner.setSelection(recipe_orig.getDimUnit());
        }
    }

    private void fillShapeSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spnConversionPanShape);
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.str_rect));
        list.add(getString(R.string.str_squa));
        list.add(getString(R.string.str_circ));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                                          android.R.layout.simple_spinner_item,
                                                          list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner != null) {
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
        }
        shapeSelection = ShapeType.toInteger(recipe_orig.getShape());
    }

    private void updatePanShapeFields(int selection) {
        View v = findViewById(R.id.layoutConvertedShapeRect);
        if (v != null)
            v.setVisibility((selection == 0) ? View.VISIBLE : View.GONE);
        v = findViewById(R.id.layoutConvertedShapeSquare);
        if (v != null)
            v.setVisibility((selection == 1) ? View.VISIBLE : View.GONE);
        v = findViewById(R.id.layoutConvertedShapeCircle);
        if (v != null)
            v.setVisibility((selection == 2) ? View.VISIBLE : View.GONE);
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
                Toast.makeText(this, getResources().getString(R.string.toast_no_sharing), Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private String buildString() {
        //error detection
        if (recipe_conv == null || recipe_conv.getIngredients().size() == 0)
            return null;

        DecimalFormat format = new DecimalFormat(getString(R.string.double_formatting));
        //header
        String buffer = getString(R.string.txt_recipe) + " \"" + recipe_conv.getName() + "\" ";
        if (recipe_conv.isRecipeWRTPeople())
            buffer += getString(R.string.txt_for) + " " + recipe_conv.getNum_people() + " " +
                      getString(R.string.txt_people) + "\n";
        if (recipe_conv.isRecipeWRTPan()) {
            switch(recipe_conv.getShape()) {
            case SHAPE_CIRCLE:
                buffer += getString(R.string.txt_share_circle) + ": " +
                          format.format(recipe_conv.getDiameter()) + " ";
                break;
            case SHAPE_RECTANGLE:
                buffer += getString(R.string.txt_share_rectangle) + ": " +
                          format.format(recipe_conv.getSideL1()) + " " +
                          getString(R.string.txt_times) + " " +
                          format.format(recipe_conv.getSideL2()) + " ";
                break;
            case SHAPE_SQUARE:
                buffer += getString(R.string.txt_share_square) + ": " +
                          format.format(recipe_conv.getSideSQ()) + " " +
                          getString(R.string.txt_times) + " " +
                          format.format(recipe_conv.getSideSQ()) + " ";
                break;
            }
            buffer += ((recipe_conv.getDimUnit() == 0) ? getString(R.string.str_cm_abbr) : getString(R.string.str_in_abbr)) + "\n";
        }
        //body
        for (IngredientEntry ingredient : recipe_conv.getIngredients())
            buffer += format.format(ingredient.getQuantity()) + " " +
                      UnitType.toString(ingredient.getUnit()) + " " +
                      getString(R.string.txt_separator) + " " +
                      ingredient.getName() + "\n";
        return buffer;
    }

    private void updateRecipe () throws WrongInputs {
        if (recipe_orig.isRecipeWRTPeople()) {
            EditText editText = (EditText) findViewById(R.id.txtConvertedRecipePeople);
            if (editText != null) {
                int new_people = Integer.parseInt(editText.getText().toString());
                if (new_people > 0)
                    recipe_conv.setNum_people(new_people);
                else
                    throw new WrongInputs();
            }
        }
        if (recipe_orig.isRecipeWRTPan()) {
            EditText editText1;
            EditText editText2;
            Spinner spinner = (Spinner) findViewById(R.id.spnConversionUnit);
            if (spinner != null)
                recipe_conv.setDimUnit(spinner.getSelectedItemPosition());
            recipe_conv.setShape(ShapeType.fromInteger(shapeSelection));
            switch (recipe_conv.getShape()) {
            case SHAPE_CIRCLE:
                editText1 = (EditText) findViewById(R.id.txtConvertedRecipeDiameter);
                if (editText1 != null) {
                    double new_diameter = Double.parseDouble(editText1.getText().toString());
                    if (new_diameter > 0.0)
                        recipe_conv.setDiameter(new_diameter);
                    else
                        throw new WrongInputs();
                }
                break;
            case SHAPE_RECTANGLE:
                editText1 = (EditText) findViewById(R.id.txtConvertedRecipeSide1);
                editText2 = (EditText) findViewById(R.id.txtConvertedRecipeSide2);
                if (editText1 != null && editText2 != null) {
                    double new_side1 = Double.parseDouble(editText1.getText().toString());
                    double new_side2 = Double.parseDouble(editText2.getText().toString());
                    if (new_side1 > 0.0 && new_side2 > 0.0) {
                        recipe_conv.setSideL1(new_side1);
                        recipe_conv.setSideL2(new_side2);
                    } else
                        throw new WrongInputs();
                }
                break;
            case SHAPE_SQUARE:
                editText1 = (EditText) findViewById(R.id.txtConvertedRecipeSide);
                if (editText1 != null) {
                    double new_side = Double.parseDouble(editText1.getText().toString());
                    if (new_side > 0.0)
                        recipe_conv.setSideSQ(new_side);
                    else
                        throw new WrongInputs();
                }
                break;
            }
            recipe_conv.scaleSides();
        }
        double factor = 1.0;
        if (recipe_orig.isRecipeWRTPeople())
            factor = (double) recipe_conv.getNum_people() / recipe_orig.getNum_people();
        if (recipe_orig.isRecipeWRTPan())
            factor = recipe_conv.getSurfaceCM2() / recipe_orig.getSurfaceCM2();
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
            } catch (NullPointerException e) {
                Toast.makeText(this, getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_LONG).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_LONG).show();
            } catch (WrongInputs e) {
                Toast.makeText(this, getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_LONG).show();
            }
        }
    }

}
