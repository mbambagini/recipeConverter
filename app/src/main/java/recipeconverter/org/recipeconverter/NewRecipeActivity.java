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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import recipeconverter.org.recipeconverter.dao.RecipeDAO;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;
import recipeconverter.org.recipeconverter.dao.ShapeType;
import recipeconverter.org.recipeconverter.exception.EntryError;
import recipeconverter.org.recipeconverter.exception.EntryNotFound;
import recipeconverter.org.recipeconverter.exception.RecipeAlreadyPresent;
import recipeconverter.org.recipeconverter.exception.WrongInputs;

public class NewRecipeActivity extends AppCompatActivity {

    static final int _ONLY_PEOPLE = 0;
    static final int _ONLY_PAN = 1;
    static final int _NO_CHOICE = 2;
    private int configuration_recipe = _NO_CHOICE;
    static final int _SHAPE_RECTANGLE = 0;
    private int configuration_shape = _SHAPE_RECTANGLE;
    static final int _SHAPE_SQUARE = 1;
    static final int _SHAPE_CIRCLE = 2;

    private long recipe_to_update_id = -1;
    private String recipe_name = null;

    private int configuration_unit = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        loadRecipeToUpdate();

        //configure shape pan menu
        configureShapeMenu();
        //set input fields to be shown
        setVisibleItems();
        //set font
        TextView myTextView = (TextView) findViewById(R.id.txt_new_recipe_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), getString(R.string.font_handwritten));
        if (myTextView != null)
            myTextView.setTypeface(typeFace);
    }

    private void loadRecipeToUpdate() {
        Bundle b = getIntent().getExtras();
        if (b == null)
            return;
        recipe_to_update_id = b.getLong(getString(R.string.intent_id), -1);
        if (recipe_to_update_id == -1)
            return;
        setTitle(getString(R.string.title_activity_main2));
        try {
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            RecipeEntry recipe = recipeDAO.getRecipe(recipe_to_update_id);
            recipeDAO.close();
            setFieldsFromRecipe(recipe);
        } catch (EntryNotFound e) {
            recipe_to_update_id = -1;
        } catch (EntryError e) {
            recipe_to_update_id = -1;
        } catch (SQLException e) {
            recipe_to_update_id = -1;
        }
    }

    private void setFieldsFromRecipe(RecipeEntry r) throws EntryError {
        EditText editText = (EditText) findViewById(R.id.txtRecipeName);
        if (editText != null)
            editText.setText(r.getName());
        recipe_name = r.getName();
        if (r.isRecipeWRTPeople()) {
            configuration_recipe = _ONLY_PEOPLE;
            editText = (EditText) findViewById(R.id.txtRecipePeople);
            if (editText != null)
                editText.setText(String.valueOf(r.getNum_people()));
        }
        if (r.isRecipeWRTPan()) {
            configuration_recipe = _ONLY_PAN;
            configuration_shape = ShapeType.toInteger(r.getShape());
            configuration_unit = r.getDimUnit();
            DecimalFormat format = new DecimalFormat(getString(R.string.double_formatting));
            editText = (EditText) findViewById(R.id.txtRecipeSide1);
            if (editText != null)
                editText.setText(format.format(r.getSideL1()).replace(',', '.'), TextView.BufferType.EDITABLE);
            editText = (EditText) findViewById(R.id.txtRecipeSide2);
            if (editText != null)
                editText.setText(format.format(r.getSideL2()).replace(',', '.'), TextView.BufferType.EDITABLE);
            editText = (EditText) findViewById(R.id.txtRecipeSide);
            if (editText != null)
                editText.setText(format.format(r.getSideSQ()).replace(',', '.'), TextView.BufferType.EDITABLE);
            editText = (EditText) findViewById(R.id.txtRecipeDiameter);
            if (editText != null)
                editText.setText(format.format(r.getDiameter()).replace(',', '.'), TextView.BufferType.EDITABLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_new_recipe, menu);
        return true;
    }

    private void configureShapeMenu() {
        //filling pan shape menu
        Spinner spinner = (Spinner) findViewById(R.id.txtRecipePan);
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.str_rect));
        list.add(getString(R.string.str_squa));
        list.add(getString(R.string.str_circ));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
            spinner.setSelection(configuration_shape);

            fillUnitSpinner();

            //added callback which updates the shown pan inputs
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    setPanShape(pos);
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }

    private void fillUnitSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spnRecipeUnit);
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.str_cm));
        list.add(getString(R.string.str_in));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spinner != null) {
            spinner.setAdapter(adapter);
            spinner.setSelection(configuration_unit);

            //added callback which updates the shown pan inputs
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    configuration_unit = pos;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }

    private RecipeEntry readInputs() throws WrongInputs, RecipeAlreadyPresent {
        RecipeEntry recipe = new RecipeEntry();

        //error early detection
        if (configuration_recipe == _NO_CHOICE)
            throw new WrongInputs();
        //name
        String name = "";
        EditText editText = (EditText) findViewById(R.id.txtRecipeName);
        if (editText != null)
            name = editText.getText().toString();
        if (name.isEmpty())
            throw new WrongInputs();
        //check if the name is already used
        if (recipe_to_update_id == -1 || name.compareTo(recipe_name) != 0) {
            boolean alreadyPresent;
            try {
                RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
                recipeDAO.open();
                alreadyPresent = recipeDAO.recipeAlreadyPresent(name);
                recipeDAO.close();
            } catch (SQLException e) {
                throw new WrongInputs();
            }
            if (alreadyPresent)
                throw new RecipeAlreadyPresent();
        }
        recipe.setName(name);
        //people
        if (configuration_recipe == _ONLY_PEOPLE) {
            editText = (EditText) findViewById(R.id.txtRecipePeople);
            if (editText != null) {
                int n_people = Integer.parseInt(editText.getText().toString());
                if (n_people <= 0 || n_people >= 100) throw new WrongInputs();
                recipe.setNum_people(n_people);
                recipe.setShape(ShapeType.SHAPE_NOT_VALID);
            }
        }
        //pan
        if (configuration_recipe == _ONLY_PAN) {
            recipe.setShape(ShapeType.fromInteger(configuration_shape));
            recipe.setDimUnit(configuration_unit);
            switch (configuration_shape) {
                case _SHAPE_RECTANGLE:
                    editText = (EditText)findViewById(R.id.txtRecipeSide1);
                    double side1 = -1.0;
                    if (editText != null)
                        side1 =Double.parseDouble(editText.getText().toString());
                    editText = (EditText)findViewById(R.id.txtRecipeSide2);
                    double side2 = -1.0;
                    if (editText != null)
                        side2 = Double.parseDouble(editText.getText().toString());
                    if (side1 <= 0.0 || side2 <= 0.0) throw new WrongInputs();
                    recipe.setSideL1(side1);
                    recipe.setSideL2(side2);
                    break;
                case _SHAPE_SQUARE:
                    editText = (EditText)findViewById(R.id.txtRecipeSide);
                    if (editText != null) {
                        double side = Double.parseDouble(editText.getText().toString());
                        if (side <= 0.0) throw new WrongInputs();
                        recipe.setSideSQ(side);
                    }
                    break;
                case _SHAPE_CIRCLE:
                    editText = (EditText)findViewById(R.id.txtRecipeDiameter);
                    if (editText != null) {
                        double diameter = Double.parseDouble(editText.getText().toString());
                        if (diameter <= 0.0) throw new WrongInputs();
                        recipe.setDiameter(diameter);
                    }
                    break;
                default:
                    throw new WrongInputs();
            }
            recipe.scaleSides();
        }
        return recipe;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void setPanShape(int id) {
        if (id != _SHAPE_RECTANGLE && id != _SHAPE_SQUARE && id != _SHAPE_CIRCLE)
            id = _SHAPE_RECTANGLE;
        configuration_shape = id;
        setVisibleItems();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPeople:
                configuration_recipe = _ONLY_PEOPLE;
                setVisibleItems();
                break;
            case R.id.btnPan:
                configuration_recipe = _ONLY_PAN;
                setVisibleItems();
                break;
            case R.id.btnAddRecipe:
                RecipeEntry recipe;
                try {
                    recipe = readInputs();
                } catch (WrongInputs e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_SHORT).show();
                    return;
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_SHORT).show();
                    return;
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_wrong_input), Toast.LENGTH_SHORT).show();
                    return;
                } catch (RecipeAlreadyPresent e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_duplicated_recipe), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(NewRecipeActivity.this, IngredientActivity.class);
                intent.putExtra(getString(R.string.intent_id), recipe_to_update_id);
                intent.putExtra(getString(R.string.intent_name), recipe.getName());
                intent.putExtra(getString(R.string.intent_people), recipe.getNum_people());
                intent.putExtra(getString(R.string.intent_shape), ShapeType.toInteger(recipe.getShape()));
                if (recipe.getShape() != ShapeType.SHAPE_NOT_VALID) {
                    intent.putExtra(getString(R.string.intent_unit), recipe.getDimUnit());
                    switch (recipe.getShape()) {
                        case SHAPE_RECTANGLE:
                            intent.putExtra(getString(R.string.intent_side1), recipe.getSideL1());
                            intent.putExtra(getString(R.string.intent_side2), recipe.getSideL2());
                            break;
                        case SHAPE_SQUARE:
                            intent.putExtra(getString(R.string.intent_side), recipe.getSideSQ());
                            break;
                        case SHAPE_CIRCLE:
                            intent.putExtra(getString(R.string.intent_diameter), recipe.getDiameter());
                            break;
                    }
                }
                startActivity(intent);
                finish();
        }
    }

    private void setVisibleItems() {
        //error checks
        if (configuration_recipe != _ONLY_PEOPLE && configuration_recipe != _ONLY_PAN)
            configuration_recipe = _NO_CHOICE;
        if (configuration_shape < _SHAPE_RECTANGLE || configuration_shape > _SHAPE_CIRCLE)
            configuration_shape = _SHAPE_RECTANGLE;
        //name field
        View v = findViewById(R.id.layoutNewRecipeName);
        if (v != null)
            v.setVisibility((configuration_recipe != _NO_CHOICE) ? View.VISIBLE : View.GONE);
        //people/shape fields
        v = findViewById(R.id.layoutHowManyPeople);
        if (v != null)
            v.setVisibility((configuration_recipe == _ONLY_PEOPLE) ? View.VISIBLE : View.GONE);
        v = findViewById(R.id.layoutPan);
        if (v != null)
            v.setVisibility((configuration_recipe == _ONLY_PAN) ? View.VISIBLE : View.GONE);
        //shape type fields
        boolean enabled = (configuration_recipe == _ONLY_PAN);
        v = findViewById(R.id.layoutShapeDimUnit);
        if (v != null)
            v.setVisibility((enabled) ? View.VISIBLE : View.GONE);
        v = findViewById(R.id.layoutShapeRect);
        if (v != null)
            v.setVisibility((enabled && configuration_shape == _SHAPE_RECTANGLE) ? View.VISIBLE : View.GONE);
        v = findViewById(R.id.layoutShapeSquare);
        if (v != null)
            v.setVisibility((enabled && configuration_shape == _SHAPE_SQUARE) ? View.VISIBLE : View.GONE);
        v = findViewById(R.id.layoutShapeCircle);
        if (v != null)
            v.setVisibility((enabled && configuration_shape == _SHAPE_CIRCLE) ? View.VISIBLE : View.GONE);
        //button
        v = findViewById(R.id.btnAddRecipe);
        if (v != null)
            v.setVisibility((configuration_recipe == _NO_CHOICE) ? View.GONE : View.VISIBLE);
    }
}
