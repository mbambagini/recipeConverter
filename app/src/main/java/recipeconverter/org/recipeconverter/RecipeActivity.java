package recipeconverter.org.recipeconverter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
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

    private String name = null;
    private ArrayList<RecipeEntry> recipes = null;
    private RecipeAdapter adapter = null;

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

        TextView myTextView = (TextView) findViewById(R.id.txt_recipe_headline);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/JennaSue.ttf");
        myTextView.setTypeface(typeFace);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe, menu);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.action_recipe_activity_search).getActionView();
        search.setIconifiedByDefault(false);
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                name = query;
                showRecipes();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                name = query;
                showRecipes();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
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
        showRecipes();
    }

    private ArrayList<RecipeEntry> updateRecipeList() {
        try {
            RecipeDAO recipeDAO = new RecipeDAO(getApplicationContext());
            recipeDAO.open();
            ArrayList<RecipeEntry> result = (ArrayList<RecipeEntry>) recipeDAO.getRecipes(name);
            recipeDAO.close();
            return result;
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_internal_error),
                    Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private void showRecipes() {
        if (recipes == null)
            recipes = new ArrayList<>();
        else
            recipes.clear();
        recipes.addAll(updateRecipeList());
        if (adapter == null) {
            adapter = new RecipeAdapter(this, android.R.layout.simple_list_item_1, recipes);
            ListView lst = (ListView) findViewById(R.id.lst_recipes);
            lst.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
    }
}
