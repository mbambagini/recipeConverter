package recipeconverter.org.recipeconverter.dao;

public enum ShapeType {
    SHAPE_NOT_VALID,
    SHAPE_RECTANGLE,
    SHAPE_SQUARE,
    SHAPE_CIRCLE;

    public static ShapeType fromInteger (int par) {
        switch(par) {
            case 0: return SHAPE_RECTANGLE;
            case 1: return SHAPE_SQUARE;
            case 2: return SHAPE_CIRCLE;
        }
        return SHAPE_NOT_VALID;
    }

    public static int toInteger (ShapeType par) {
        switch(par) {
            case SHAPE_RECTANGLE: return 0;
            case SHAPE_SQUARE: return 1;
            case SHAPE_CIRCLE: return 2;
        }
        return -1;
    }
}
