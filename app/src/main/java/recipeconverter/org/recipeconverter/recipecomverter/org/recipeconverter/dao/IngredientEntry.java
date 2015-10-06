package recipeconverter.org.recipeconverter.recipecomverter.org.recipeconverter.dao;

/**
 * Created by mario on 06/10/15.
 */
public class IngredientEntry {

    private int id;
    private String name;
    private double quantity;
    private int unit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }
}
