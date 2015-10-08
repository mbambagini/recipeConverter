package recipeconverter.org.recipeconverter.dao;

/**
 * Created by mario on 06/10/15.
 */
public class IngredientEntry {

    private long id;
    private String name;
    private double quantity;
    private UnitType unit;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }
}
