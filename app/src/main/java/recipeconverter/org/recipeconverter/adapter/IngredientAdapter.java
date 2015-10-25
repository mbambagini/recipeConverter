package recipeconverter.org.recipeconverter.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import recipeconverter.org.recipeconverter.R;
import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.UnitType;

public class IngredientAdapter extends ArrayAdapter<IngredientEntry> {

    private static Typeface typeFace = null;

    private boolean enableDelete = true;

    private Activity context;
    private List<IngredientEntry> ingredients;

    public IngredientAdapter(Activity context, int textViewResourceId, List<IngredientEntry> values, boolean enableDelete) {
        super(context, textViewResourceId, values);
        this.enableDelete = enableDelete;
        this.context = context;
        ingredients = values;
        if (typeFace == null)
            typeFace = Typeface.createFromAsset(context.getAssets(),
                                                context.getString(R.string.font_handwritten));
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) { //reuse view
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_ingredient, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.txt_ingredient_name);
            viewHolder.quantity = (TextView) view.findViewById(R.id.txt_ingredient_quantity);
            viewHolder.unit = (TextView) view.findViewById(R.id.txt_ingredient_unit);
            viewHolder.id = (TextView) view.findViewById(R.id.txt_ingredient_id);
            viewHolder.btn = (ImageButton) view.findViewById(R.id.btnDeleteIngredient);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(ingredients.get(position).getName());
        holder.name.setTypeface(typeFace);
        DecimalFormat format = new DecimalFormat(getContext().getString(R.string.double_formatting));
        String formatDouble = format.format(ingredients.get(position).getQuantity());
        holder.quantity.setText(formatDouble);
        holder.unit.setText(UnitType.toString(ingredients.get(position).getUnit()));
        holder.id.setText(Long.toString(ingredients.get(position).getId()));
        if (enableDelete) {
            holder.btn.setVisibility(View.VISIBLE);
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    ingredients.remove(position);
                    notifyDataSetChanged();
                }
            });
        } else
            holder.btn.setVisibility(View.GONE);
        return view;
    }

    static class ViewHolder {
        public TextView name;
        public TextView quantity;
        public TextView unit;
        public TextView id;
        public ImageButton btn;
    }

}
