package recipeconverter.org.recipeconverter.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import recipeconverter.org.recipeconverter.R;
import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.UnitType;

public class IngredientAdapter extends ArrayAdapter<IngredientEntry> {

    private static Typeface typeFace = null;

    private final Activity context;
    private final List<IngredientEntry> ingredients;

    public IngredientAdapter(Activity context, int textViewResourceId, List<IngredientEntry> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        ingredients = values;
        if (typeFace == null)
            typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/JennaSue.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) { //reuse view
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_ingredient, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.txt_ingredient_name);
            viewHolder.quantity = (TextView) view.findViewById(R.id.txt_ingredient_quantity);
            viewHolder.unit = (TextView) view.findViewById(R.id.txt_ingredient_unit);
            viewHolder.id = (TextView) view.findViewById(R.id.txt_ingredient_id);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(ingredients.get(position).getName());
        holder.name.setTypeface(typeFace);
        DecimalFormat format = new DecimalFormat("0.00");
        String formatDouble = format.format(ingredients.get(position).getQuantity());
        holder.quantity.setText(formatDouble);
        holder.unit.setText(UnitType.toString(ingredients.get(position).getUnit()));
        holder.id.setText(Long.toString(ingredients.get(position).getId()));

        return view;
    }

    static class ViewHolder {
        public TextView name;
        public TextView quantity;
        public TextView unit;
        public TextView id;
    }

}
