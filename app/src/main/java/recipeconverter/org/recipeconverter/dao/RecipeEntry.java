package recipeconverter.org.recipeconverter.dao;

import java.util.ArrayList;
import java.util.List;

public class RecipeEntry {

    public static final double inch_to_cm_ = 2.54;

    public static final double pi_ = 3.14;

    private long id;
    private String name;
    private int num_people = -1;
    private ShapeType shape = ShapeType.SHAPE_NOT_VALID;
    private double sideL1 = -1.0;
    private double sideL2 = -1.0;
    private double sideSQ = -1.0;
    private double diameter = -1.0;

    /**
     * dimUnit == 0 means centimeters, 1 means inches
     */
    private int dimUnit = 0;
    private List<IngredientEntry> ingredients = new ArrayList<IngredientEntry>();

    /** \brief return if the recipe is reported with respect to number of people
     */
    public boolean isRecipeWRTPeople () {
        return (num_people > 0);
    }

    /** \brief return if the recipe is reported with respect to the pan type/size
     */
    public boolean isRecipeWRTPan () {
        return ((num_people < 1) && (shape != ShapeType.SHAPE_NOT_VALID));
    }

    /** \brief return the surface in square centimeter
     *
     * @return surface in cm^2
     */
    public double getSurfaceCM2 () {
        if (!isRecipeWRTPan())
            return -1.0;

        double scale_factor = (dimUnit == 0) ? 1.0 : inch_to_cm_;
        switch (shape) {
            case SHAPE_RECTANGLE:
                return sideL1 * sideL2 * scale_factor * scale_factor;
            case SHAPE_SQUARE:
                return sideSQ * sideSQ * scale_factor * scale_factor;
            case SHAPE_CIRCLE:
                return (diameter / 2) * (diameter / 2) * scale_factor * scale_factor * pi_;
        }

        return -1.0;
    }

    /**
     * once a shape is configured, set other parameters (of other shapes) to provide the same area
     */
    public void scaleSides () {
        if (isRecipeWRTPeople() && shape != ShapeType.SHAPE_NOT_VALID)
            return;
        double actualSurface = getSurface();
        switch (shape) {
            case SHAPE_CIRCLE:
                sideSQ = Math.sqrt(actualSurface);
                sideL1 = sideSQ;
                sideL2 = sideSQ;
                break;
            case SHAPE_SQUARE:
                sideL1 = sideSQ;
                sideL2 = sideSQ;
                diameter = Math.sqrt(actualSurface / pi_) * 2;
                break;
            case SHAPE_RECTANGLE:
                diameter = Math.sqrt(actualSurface / pi_) * 2;
                sideSQ = Math.sqrt(sideL1 * sideL2);
                break;
        }
    }

    /** \brief return the surface in its measurement unit (square inch or square centimeter)
     *
     * @return surface in its own unit
     */
    private double getSurface() {
        if (!isRecipeWRTPan())
            return -1.0;
        switch (shape) {
            case SHAPE_RECTANGLE:
                return sideL1 * sideL2;
            case SHAPE_SQUARE:
                return sideSQ * sideSQ;
            case SHAPE_CIRCLE:
                return (diameter / 2) * (diameter / 2) * pi_;
        }
        return -1.0;
    }

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

    public int getNum_people() {
        return num_people;
    }

    public void setNum_people(int num_people) {
        this.num_people = num_people;
    }

    public double getSideL1() {
        return sideL1;
    }

    public void setSideL1(double sideL1) {
        this.sideL1 = sideL1;
    }

    public double getSideL2() {
        return sideL2;
    }

    public void setSideL2(double sideL2) {
        this.sideL2 = sideL2;
    }

    public double getSideSQ() {
        return sideSQ;
    }

    public void setSideSQ(double sideSQ) {
        this.sideSQ = sideSQ;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public List<IngredientEntry> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientEntry> ingredients) {
        this.ingredients = ingredients;
    }

    public ShapeType getShape() {
        return shape;
    }

    public void setShape(ShapeType shape) {
        this.shape = shape;
    }

    public int getDimUnit() {
        return dimUnit;
    }

    public void setDimUnit(int dimUnit) {
        this.dimUnit = dimUnit;
    }

    public RecipeEntry clone() {
        RecipeEntry r = new RecipeEntry();
        r.id = id;
        r.name = name;
        r.num_people = num_people;
        r. shape = shape;
        r.sideL1 = sideL1;
        r.sideL2 = sideL2;
        r.sideSQ = sideSQ;
        r.diameter = diameter;
        r.dimUnit = dimUnit;
        for (IngredientEntry ingredient : ingredients)
            r.ingredients.add(ingredient.clone());
        return r;
    }
}
