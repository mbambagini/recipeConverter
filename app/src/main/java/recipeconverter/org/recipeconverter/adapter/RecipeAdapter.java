package recipeconverter.org.recipeconverter.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import recipeconverter.org.recipeconverter.R;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;

public class RecipeAdapter extends ArrayAdapter<RecipeEntry> {

    private static Typeface typeFace = null;

    private final Activity context;
    private final List<RecipeEntry> recipes;

    public RecipeAdapter(Activity context, int textViewResourceId, List<RecipeEntry> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        recipes = values;
        if (typeFace == null)
            typeFace = Typeface.createFromAsset(context.getAssets(), "fonts/JennaSue.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;

        View view = convertView;
        if (view == null) { //reuse view
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.item_recipe, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.txt_recipe_name);
            viewHolder.id = (TextView) view.findViewById(R.id.txt_recipe_id);
            viewHolder.pan = (ImageView) view.findViewById(R.id.imgIconPan);
            viewHolder.people = (ImageView) view.findViewById(R.id.imgIconPeople);
//            viewHolder.btnEdit = (ImageButton) view.findViewById(R.id.btnEditRecipe);
//            viewHolder.btnDelete = (ImageButton) view.findViewById(R.id.btnDeleteRecipe);
            view.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(recipes.get(position).getName());
/*
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            RecipeDAO recipeDAO = new RecipeDAO(getContext());
                            recipeDAO.open();
                            recipeDAO.deleteRecipe(recipes.get(pos).getId());
                            recipeDAO.close();
                            recipes.remove(pos);
                            notifyDataSetChanged();
                        } catch (EntryNotFound | EntryError | SQLException e) {
                            Toast.makeText(getContext(),
                                "Error",
                                Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.setMessage("are you sure?").setTitle("are you sure?");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(arg0.getContext(), NewRecipeActivity.class);
                intent.putExtra("id", recipes.get(pos).getId());
                getContext().startActivity(intent);
            }
        });
*/
        holder.name.setTypeface(typeFace);

        boolean en_people = recipes.get(position).getNum_people() > 0;
        holder.people.setVisibility(en_people ? View.VISIBLE : View.GONE);
        holder.pan.setVisibility(en_people ? View.GONE : View.VISIBLE);

        holder.id.setText(Long.toString(recipes.get(position).getId()));
/*
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ConversionActivity.class);
                intent.putExtra("id", recipes.get(pos).getId());
                getContext().startActivity(intent);
            }
        });*/

        return view;
    }

    static class ViewHolder {
        public TextView name;
        public ImageView pan;
        public ImageView people;
        public TextView id;
        //public ImageButton btnDelete;
        //public ImageButton btnEdit;
    }

}
