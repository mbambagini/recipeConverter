package recipeconverter.org.recipeconverter.dao;

import java.util.ArrayList;
import java.util.List;

public class RecipeEntry {

    private long id;
    private String name;
    private int num_people = -1;
    private ShapeType shape = ShapeType.SHAPE_NOT_VALID;
    private double side1 = -1.0;
    private double side2 = -1.0;
    private double diameter = -1.0;
    private int dimUnit = 0;
    private List<IngredientEntry> ingredients = new ArrayList<>();

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
}
