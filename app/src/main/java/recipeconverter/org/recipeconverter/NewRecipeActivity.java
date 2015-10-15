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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import recipeconverter.org.recipeconverter.dao.RecipeEntry;
import recipeconverter.org.recipeconverter.dao.RecipeDAO;
import recipeconverter.org.recipeconverter.dao.ShapeType;
import recipeconverter.org.recipeconverter.exception.WrongInputs;
import recipeconverter.org.recipeconverter.exception.RecipeAlreadyPresent;

public class NewRecipeActivity extends ActionBarActivity {

    static final int _ONLY_PEOPLE = 0;
    static final int _ONLY_PAN = 1;
    static final int _NO_CHOISE = 2;
    private int configuration_recipe = _NO_CHOISE;
    static final int _SHAPE_RECTANGLE = 0;
    private int configuration_shape = _SHAPE_RECTANGLE;
    static final int _SHAPE_SQUARE = 1;
    static final int _SHAPE_CIRCLE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //configure shape pan menu
        configureShapeMenu();
        //set input fields to be shown
        setVisibleItems();
        //set font
        TextView myTextView = (TextView) findViewById(R.id.txt_new_recipe_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/JennaSue.ttf");
        myTextView.setTypeface(typeFace);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_recipe, menu);
        return true;
    }

    private void configureShapeMenu () {
        //filling pan shape menu
        Spinner spinner = (Spinner) findViewById(R.id.txtRecipePan);
        List<String> list = new ArrayList<>();
        list.add("Rectangle"); list.add("Square"); list.add("Circle");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

    private RecipeEntry readInputs() throws WrongInputs, RecipeAlreadyPresent {
        RecipeEntry recipe = new RecipeEntry();

        //error early detection
        if (configuration_recipe == _NO_CHOISE)
            throw new WrongInputs();
        //name
        String name = ((EditText) findViewById(R.id.txtRecipeName)).getText().toString();
        if (name.compareTo("") == 0)
            throw new WrongInputs();
        //check if the name is already used
        boolean alreadyPresent = false;
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
        recipe.setName(name);
        //people
        if (configuration_recipe == _ONLY_PEOPLE) {
            int n_people = Integer.parseInt(((EditText) findViewById(R.id.txtRecipePeople)).getText().toString());
            if (n_people <= 0 || n_people >= 100) throw new WrongInputs();
            recipe.setNum_people(n_people);
            recipe.setShape(ShapeType.SHAPE_NOT_VALID);
        }
        //pan
        if (configuration_recipe == _ONLY_PAN) {
            recipe.setShape(ShapeType.fromInteger(configuration_shape));
            switch (configuration_shape) {
                case _SHAPE_RECTANGLE:
                    double side1 = Double.parseDouble(((EditText) findViewById(R.id.txtRecipeSide1)).getText().toString());
                    double side2 = Double.parseDouble(((EditText) findViewById(R.id.txtRecipeSide2)).getText().toString());
                    if (side1 <= 0.0 || side2 <= 0.0) throw new WrongInputs();
                    recipe.setSide1(side1);
                    recipe.setSide2(side2);
                    break;
                case _SHAPE_SQUARE:
                    double side = Double.parseDouble(((EditText) findViewById(R.id.txtRecipeSide)).getText().toString());
                    if (side <= 0.0) throw new WrongInputs();
                    recipe.setSide1(side);
                    break;
                case _SHAPE_CIRCLE:
                    double diameter = Double.parseDouble(((EditText) findViewById(R.id.txtRecipeDiameter)).getText().toString());
                    if (diameter <= 0.0) throw new WrongInputs();
                    recipe.setDiameter(diameter);
                    break;
                default:
                    throw new WrongInputs();
            }
        }
        return recipe;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_ingredients) {
            RecipeEntry recipe;
            try {
                recipe = readInputs();
            } catch (WrongInputs | NullPointerException | NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Set all inputs correctly", Toast.LENGTH_SHORT).show();
                return true;
            } catch (RecipeAlreadyPresent e) {
                Toast.makeText(getApplicationContext(), "Name already in use", Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent intent = new Intent(NewRecipeActivity.this, IngredientActivity.class);
            intent.putExtra("name", recipe.getName());
            intent.putExtra("num_people", recipe.getNum_people());
            intent.putExtra("shape", ShapeType.toInteger(recipe.getShape()));
            if (recipe.getShape() != ShapeType.SHAPE_NOT_VALID) {
                switch (recipe.getShape()) {
                    case SHAPE_RECTANGLE:
                        intent.putExtra("side1", recipe.getSide1());
                        intent.putExtra("side2", recipe.getSide2());
                        break;
                    case SHAPE_SQUARE:
                        intent.putExtra("side", recipe.getSide1());
                        break;
                    case SHAPE_CIRCLE:
                        intent.putExtra("diameter", recipe.getDiameter());
                        break;
                }
            }
            startActivity(intent);
            return true;
        }

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
                break;
            case R.id.btnPan:
                configuration_recipe = _ONLY_PAN;
                break;
        }
        setVisibleItems();
    }

    private void setVisibleItems() {
        //error checks
        if (configuration_recipe != _ONLY_PEOPLE && configuration_recipe != _ONLY_PAN)
            configuration_recipe = _NO_CHOISE;
        if (configuration_shape < _SHAPE_RECTANGLE || configuration_shape > _SHAPE_CIRCLE)
            configuration_shape = _SHAPE_RECTANGLE;
        //people/shape fields
        findViewById(R.id.layoutHowManyPeople).setVisibility((configuration_recipe == _ONLY_PEOPLE) ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutPan).setVisibility((configuration_recipe == _ONLY_PAN) ? View.VISIBLE : View.GONE);
        //shape type fields
        boolean enabled = (configuration_recipe == _ONLY_PAN);
        findViewById(R.id.layoutShapeRect).setVisibility((enabled && configuration_shape == _SHAPE_RECTANGLE) ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutShapeSquare).setVisibility((enabled && configuration_shape == _SHAPE_SQUARE) ? View.VISIBLE : View.GONE);
        findViewById(R.id.layoutShapeCircle).setVisibility((enabled && configuration_shape == _SHAPE_CIRCLE) ? View.VISIBLE : View.GONE);
        //padding
        findViewById(R.id.spacePadding1).setVisibility((configuration_recipe == _NO_CHOISE) ? View.INVISIBLE : View.GONE);
        findViewById(R.id.spacePadding2).setVisibility(enabled ? View.GONE : View.INVISIBLE);
    }
}
