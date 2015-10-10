package recipeconverter.org.recipeconverter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    static final int NO_CHOISE = 0;
    static final int ONLY_PEOPLE = 1;
    static final int ONLY_PAN = 2;
    static final int PAN_PEOPLE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideComponents();
        setPaggingVisibility(3);

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
                Toast.makeText(getApplicationContext(), "sel: " + pos, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_ingredients) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setPaggingVisibility(int numActive) {
        findViewById(R.id.spacePadding1).setVisibility((numActive > 0) ? View.INVISIBLE : View.GONE);
        findViewById(R.id.spacePadding2).setVisibility((numActive > 1) ? View.INVISIBLE : View.GONE);
        findViewById(R.id.spacePadding3).setVisibility((numActive > 2) ? View.INVISIBLE : View.GONE);
    }

    private void hideComponents() {
        findViewById(R.id.layoutHowManyPeople).setVisibility(View.GONE);
        findViewById(R.id.layoutPan).setVisibility(View.GONE);
        hidePanShapes();
    }

    private void hidePanShapes() {
        findViewById(R.id.layoutShapeCircle).setVisibility(View.GONE);
        findViewById(R.id.layoutShapeRect).setVisibility(View.GONE);
        findViewById(R.id.layoutShapeSquare).setVisibility(View.GONE);
    }

    public void setPanShape(int id) {
        hidePanShapes();
        switch (id) {
            case 0:
                findViewById(R.id.layoutShapeRect).setVisibility(View.VISIBLE);
                break;
            case 1:
                findViewById(R.id.layoutShapeSquare).setVisibility(View.VISIBLE);
                break;
            case 2:
                findViewById(R.id.layoutShapeCircle).setVisibility(View.VISIBLE);
                break;
            default:
                //hidePanShapes();
                //findViewById(R.id.spacePadding4).setVisibility(View.INVISIBLE);
        }
    }

    public void onClick(View v) {
        setPaggingVisibility(0);
        hidePanShapes();
        switch (v.getId()) {
            case R.id.btnPeople:
                findViewById(R.id.layoutHowManyPeople).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutPan).setVisibility(View.GONE);
                setPaggingVisibility(1);
                break;
            case R.id.btnPan:
                findViewById(R.id.layoutHowManyPeople).setVisibility(View.GONE);
                findViewById(R.id.layoutPan).setVisibility(View.VISIBLE);
                setPaggingVisibility(1);
                break;
            case R.id.btnPeoplePan:
                findViewById(R.id.layoutHowManyPeople).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutPan).setVisibility(View.VISIBLE);
                setPaggingVisibility(0);
                break;
        }
    }
}
