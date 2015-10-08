package recipeconverter.org.recipeconverter.dao;

public enum RecipeType {
    RECIPE_NOT_VALID,
    RECIPE_STARTER,
    RECIPE_FIRST_DISH,
    RECIPE_SECOND_DISH,
    RECIPE_SIDE_DISH,
    RECIPE_DESSERT,
    RECIPE_OTHER;

    public static RecipeType fromInteger (int par) {
        switch(par) {
            case 1: return RECIPE_STARTER;
            case 2: return RECIPE_FIRST_DISH;
            case 3: return RECIPE_SECOND_DISH;
            case 4: return RECIPE_SIDE_DISH;
            case 5: return RECIPE_DESSERT;
            case 6: return RECIPE_OTHER;
        }
        return RECIPE_NOT_VALID;
    }

    public static int toInteger (RecipeType par) {
        switch(par) {
            case RECIPE_NOT_VALID: return 0;
            case RECIPE_STARTER: return 1;
            case RECIPE_FIRST_DISH: return 2;
            case RECIPE_SECOND_DISH: return 3;
            case RECIPE_SIDE_DISH: return 4;
            case RECIPE_DESSERT: return 5;
            case RECIPE_OTHER: return 6;
        }
        return 0;
    }
}
