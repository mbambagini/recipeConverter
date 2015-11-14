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
    private double side1 = -1.0;
    private double side2 = -1.0;
    private double diameter = -1.0;
    private int dimUnit = 0;
    private List<IngredientEntry> ingredients = new ArrayList<>();

    public boolean isRecipeWRTPeople () {
        return (num_people > 0);
    }

    public boolean isRecipeWRTPan () {
        return ((num_people < 1) && (shape != ShapeType.SHAPE_NOT_VALID));
    }

    public double getSurfaceCM2 () {
        if (!isRecipeWRTPan())
            return -1.0;

        double scale_factor = (dimUnit == 0) ? 1.0 : inch_to_cm_;
        switch (shape) {
            case SHAPE_RECTANGLE:
                return side1 * side2 * scale_factor * scale_factor;
            case SHAPE_SQUARE:
                return side1 * side1 * scale_factor * scale_factor;
            case SHAPE_CIRCLE:
                return diameter * diameter * scale_factor * scale_factor * pi_;
        }

        return -1.0;
    }

    public double getSurface() {
        if (!isRecipeWRTPan())
            return -1.0;
        switch (shape) {
            case SHAPE_RECTANGLE:
                return side1 * side2;
            case SHAPE_SQUARE:
                return side1 * side1;
            case SHAPE_CIRCLE:
                return diameter * diameter * pi_;
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

    public double getSide1() {
        return side1;
    }

    public void setSide1(double side1) {
        this.side1 = side1;
    }

    public double getSide2() {
        return side2;
    }

    public void setSide2(double side2) {
        this.side2 = side2;
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
        r.side1 = side1;
        r.side2 = side2;
        r.diameter = diameter;
        r.dimUnit = dimUnit;
        for (IngredientEntry ingredient : ingredients)
            r.ingredients.add(ingredient.clone());
        return r;
    }

}
