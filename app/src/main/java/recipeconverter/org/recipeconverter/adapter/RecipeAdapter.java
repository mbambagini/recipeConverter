package recipeconverter.org.recipeconverter.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import recipeconverter.org.recipeconverter.R;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;

public class RecipeAdapter extends ArrayAdapter<RecipeEntry> {

    private final Activity context;
    private final List<RecipeEntry> recipes;

    public RecipeAdapter(Activity context, int textViewResourceId, List<RecipeEntry> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        recipes = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) { //reuse view
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_recipe, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.txt_recipe_name);
            viewHolder.id = (TextView) view.findViewById(R.id.txt_recipe_id);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(recipes.get(position).getName());
        holder.id.setText(Long.toString(recipes.get(position).getId()));
        return view;
    }

    static class ViewHolder {
        public TextView name;
        public TextView id;
    }

}