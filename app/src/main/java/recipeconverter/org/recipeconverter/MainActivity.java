package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    static final int NO_CHOISE = 0;
    static final int ONLY_PEOPLE = 1;
    static final int ONLY_PAN = 2;
    static final int PAN_PEOPLE = 3;

    static final int SHAPE_RECTANGLE = 0;
    static final int SHAPE_SQUARE = 1;
    static final int SHAPE_CIRCLE = 2;

    private int configuration_recipe = NO_CHOISE;
    private int configuration_shape = SHAPE_RECTANGLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner spinner = (Spinner) findViewById(R.id.txtRecipePan);
        List<String> list = new ArrayList<>();
        list.add("Rectangle");
        list.add("Square");
        list.add("Circle");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                setPanShape(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        configuration_recipe = NO_CHOISE;
        configuration_shape = SHAPE_RECTANGLE;
        setVisibleItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_ingredients) {
            Intent intent = new Intent(MainActivity.this, IngredientActivity.class);
            try {
                if (configuration_recipe == NO_CHOISE)
                    throw new Exception();
                EditText txt = (EditText)findViewById(R.id.txtRecipeName);
                if (txt.getText().toString().compare("") == 0)
                    throw new Exception();
                intent.putExtra("name", txt.getText().toString());
                intent.putExtra("recipe_type", configuration_recipe);
                intent.putExtra("shape_type", configuration_shape);
                if (configuration_recipe == ONLY_PEOPLE || configuration_recipe == PAN_PEOPLE) {
                    txt = (EditText)findViewById(R.id.txtRecipePeople);
                    int n_people = Integer.parseInt(txt.getText().toString());
                    if (n_people <= 0)
                        throw new Exception();
                    intent.putExtra("people", n_people);
                }
                if (configuration_recipe == ONLY_PAN || configuration_recipe == PAN_PEOPLE) {
                    switch (configuration_shape) {
                    case SHAPE_RECTANGLE:
                        double side1 = Double.parseDouble(((EditText)findViewById(R.id.txtRecipeSide1)).getText().toString() );
                        double side2 = Double.parseDouble(((EditText)findViewById(R.id.txtRecipeSide2)).getText().toString() );
                        if (side1 <= 0.0 || side2 <= 0.0)
                            throw new Exception();
                        intent.putExtra("side1", side1);
                        intent.putExtra("side2", side2);
                        break;
                    case SHAPE_SQUARE:
                        double side = Double.parseDouble(((EditText)findViewById(R.id.txtRecipeSide)).getText().toString() );
                        if (side <= 0.0 )
                            throw new Exception();
                        intent.putExtra("side", side);
                        break;
                    case SHAPE_CIRCLE:
                        double diamiter = Double.parseDouble(((EditText)findViewById(R.id.txtRecipeDiameter)).getText().toString() );
                        if (diamiter <= 0.0 )
                            throw new Exception();
                        intent.putExtra("diameter", diamiter);
                        break;
                    default:
                        throw new Exception();
                    }
                }
            } catch (Exception e) {
                return true;
            }
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setPanShape(int id) {
        if (id < SHAPE_RECTANGLE || id > SHAPE_CIRCLE)
            id = SHAPE_RECTANGLE;
        configuration_shape = id;
        setVisibleItems();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPeople:
                configuration_recipe = ONLY_PEOPLE;
                break;
            case R.id.btnPan:
                configuration_recipe = ONLY_PAN;
                break;
            case R.id.btnPeoplePan:
                configuration_recipe = PAN_PEOPLE;
                break;
        }
        setVisibleItems();
    }

    private void setVisibleItems () {
        //error checks
        if (configuration_recipe < NO_CHOISE || configuration_recipe > PAN_PEOPLE)
          configuration_recipe = NO_CHOISE;
        if (configuration_shape < SHAPE_RECTANGLE || configuration_shape > SHAPE_CIRCLE)
          configuration_shape = SHAPE_RECTANGLE;

        //people/shape fields
        findViewById(R.id.layoutHowManyPeople).setVisibility((configuration_recipe == ONLY_PEOPLE ||
                                                              configuration_recipe == PAN_PEOPLE) ?
                                                             View.VISIBLE : View.GONE);
        findViewById(R.id.layoutPan).setVisibility((configuration_recipe == ONLY_PAN ||
                                                    configuration_recipe == PAN_PEOPLE) ?
                                                   View.VISIBLE : View.GONE);

        //shape type fields
        boolean enabled = (configuration_recipe == ONLY_PAN || configuration_recipe == PAN_PEOPLE);
        findViewById(R.id.layoutShapeCircle).setVisibility((enabled && configuration_shape == SHAPE_RECTANGLE) ?
                                                           View.VISIBLE : View.GONE);
        findViewById(R.id.layoutShapeRect).setVisibility((enabled && configuration_shape == SHAPE_SQUARE) ?
                                                         View.VISIBLE : View.GONE);
        findViewById(R.id.layoutShapeSquare).setVisibility((enabled && configuration_shape == SHAPE_CIRCLE) ?
                                                           View.VISIBLE : View.GONE);

        //padding
        findViewById(R.id.spacePadding1).setVisibility((configuration_recipe == ONLY_PEOPLE ||
                                                        configuration_recipe == PAN_PEOPLE) ?
                                                       View.GONE : View.INVISIBLE);
        findViewById(R.id.spacePadding2).setVisibility(enabled ? View.GONE : View.INVISIBLE);
        findViewById(R.id.spacePadding3).setVisibility(enabled ? View.GONE : View.INVISIBLE);
    }
}
