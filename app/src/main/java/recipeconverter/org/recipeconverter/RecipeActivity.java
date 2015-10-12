package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import recipeconverter.org.recipeconverter.adapter.RecipeAdapter;
import recipeconverter.org.recipeconverter.dao.RecipeDAO;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;


public class RecipeActivity extends ActionBarActivity {

    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        try {
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            ArrayList<RecipeEntry> recipes = (ArrayList<RecipeEntry>) recipeDAO.getRecipes();
            recipeDAO.close();
            if (recipes == null)
                return;
            ListView lst = (ListView) findViewById(R.id.lst_recipes);
            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView txt = (TextView) view.findViewById(R.id.txt_recipe_id);
                    Toast.makeText(getApplicationContext(), "aaa", Toast.LENGTH_LONG).show();
                    //if (txt != null) {
                    Intent intent = new Intent(RecipeActivity.this, ConversionActivity.class);
                    intent.putExtra("id", Long.parseLong(txt.getText().toString()));
                    finish();
                    startActivity(intent);
                    //}
                }
            });
            adapter = new RecipeAdapter(this, android.R.layout.simple_list_item_1, recipes);
            lst.setAdapter(adapter);
            Toast.makeText(getApplicationContext(), "v", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
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
