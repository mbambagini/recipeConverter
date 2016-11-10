package recipeconverter.org.recipeconverter;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import recipeconverter.org.recipeconverter.dao.IngredientEntry;
import recipeconverter.org.recipeconverter.dao.RecipeEntry;
import recipeconverter.org.recipeconverter.dao.ShapeType;
import recipeconverter.org.recipeconverter.dao.UnitType;

public class ConversionTest  {

    @Test
    public void checkConversion () {
        RecipeEntry r = new RecipeEntry();
        ArrayList<IngredientEntry> l = new ArrayList<IngredientEntry>();
        IngredientEntry i = new IngredientEntry();
        i.setId(1);
        i.setName("Test1");
        i.setQuantity(10);
        i.setUnit(UnitType.UNIT_CENTIMETER);
        l.add(i);
        i.setId(2);
        i.setName("Test2");
        i.setQuantity(20);
        i.setUnit(UnitType.UNIT_CENTIMETER);
        l.add(i);
        i.setId(3);
        i.setName("Test3");
        i.setQuantity(30);
        i.setUnit(UnitType.UNIT_CENTIMETER);
        l.add(i);
        r.setShape(ShapeType.SHAPE_CIRCLE);
        r.setDiameter(10.0);
        r.setIngredients(l);
        r.setDimUnit(1); //Assume inches
        r.setName("RecipeTest");
        Assert.assertEquals(1506.45, r.getSurfaceCM2(), 0.1);
    }


    @Test
    public void checkConversion2 () {
    }
}
