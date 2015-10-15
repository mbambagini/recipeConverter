package recipeconverter.org.recipeconverter;

import android.content.Intent;
import android.graphics.Typeface;
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

    private RecipeAdapter adapter = null;
    private ArrayList<RecipeEntry> recipes = new ArrayList<RecipeEntry>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        ListView lst = (ListView) findViewById(R.id.lst_recipes);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt = (TextView) view.findViewById(R.id.txt_recipe_id);
                if (txt != null) {
                    Intent intent = new Intent(RecipeActivity.this, ConversionActivity.class);
                    intent.putExtra("id", Long.parseLong(txt.getText().toString()));
                    startActivity(intent);
                }
            }
        });
        adapter = new RecipeAdapter(this, android.R.layout.simple_list_item_1, recipes);
        lst.setAdapter(adapter);

        TextView myTextView = (TextView) findViewById(R.id.txt_recipe_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/JennaSue.ttf");
        myTextView.setTypeface(typeFace);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_recipe_activity_add) {
            Intent intent = new Intent(RecipeActivity.this, NewRecipeActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateRecipeList();
        if (recipes != null)
            adapter.notifyDataSetChanged();
        // The activity has become visible (it is now "resumed").
    }

    private updateRecipeList () {
        try {
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            recipes = (ArrayList<RecipeEntry>) recipeDAO.getRecipes();
            recipeDAO.close();
        } catch (SQLException e) {
            recipes = null;
            Toast.makeText(getApplicationContext(), "internal error", Toast.LENGTH_LONG).show();
        }
    }
}
