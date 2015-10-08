package recipeconverter.org.recipeconverter.dao;

public enum ShapeType {
    SHAPE_NOT_VALID,
    SHAPE_RECTANGLE,
    SHAPE_SQUARE,
    SHAPE_CIRCLE;

    public static ShapeType fromInteger (int par) {
        switch(par) {
            case 1: return SHAPE_RECTANGLE;
            case 2: return SHAPE_SQUARE;
            case 3: return SHAPE_CIRCLE;
        }
        return SHAPE_NOT_VALID;
    }

    public static int toInteger (ShapeType par) {
        switch(par) {
            case SHAPE_NOT_VALID: return 0;
            case SHAPE_RECTANGLE: return 1;
            case SHAPE_SQUARE: return 2;
            case SHAPE_CIRCLE: return 3;
        }
        return 0;
    }
}
