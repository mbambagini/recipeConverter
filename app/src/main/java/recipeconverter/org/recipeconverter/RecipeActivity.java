package recipeconverter.org.recipeconverter;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

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

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "convert" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0x00, 0xFF, 0x00)));
                //openItem.setWidth(90);
                //openItem.setTitle("Edit");
                //openItem.setTitleSize(18);
                //openItem.setTitleColor(Color.WHITE);
                openItem.setIcon(android.R.drawable.ic_menu_preferences);
                menu.addMenuItem(openItem);

                // create "edit" item
                SwipeMenuItem editItem = new SwipeMenuItem(getApplicationContext());
                editItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0xFF, 0xFF)));
                //editItem.setWidth(90);
                //editItem.setTitle("Edit");
                //editItem.setTitleSize(18);
                //editItem.setTitleColor(Color.WHITE);
                editItem.setIcon(android.R.drawable.ic_menu_edit);
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xFF, 0x00, 0x00)));
                //deleteItem.setWidth(90);
                deleteItem.setIcon(android.R.drawable.ic_menu_delete);
                menu.addMenuItem(deleteItem);
            }
        };

        SwipeMenuListView lst = (SwipeMenuListView) findViewById(R.id.lst_recipes);
        lst.setMenuCreator(creator);
/*        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
*/
        lst.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Intent intent;
                switch (index) {
                    case 0:
                        // open
                        intent = new Intent(getContext(), ConversionActivity.class);
                        intent.putExtra("id", recipes.get(index).getId());
                        getContext().startActivity(intent);
                        break;
                    case 1:
                        // edit
                        intent = new Intent(arg0.getContext(), NewRecipeActivity.class);
                        intent.putExtra("id", recipes.get(index).getId());
                        getContext().startActivity(intent);
                        break;
                    case 1:
                        // delete
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
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
