package recipeconverter.org.recipeconverter.recipecomverter.org.recipeconverter.dao;

public enum UnitType {
    //WEIGHT
    UNIT_NOT_VALID,
    UNIT_OUNCE,
    UNIT_POUND,
    UNIT_GRAM,
    UNIT_KILOGRAM,
    //CAPACITY
    UNIT_GALLON,
    UNIT_LITRE;

    public static UnitType fromInteger (int par) {
        switch(par) {
            case 1: return UNIT_OUNCE;
            case 2: return UNIT_POUND;
            case 3: return UNIT_GRAM;
            case 4: return UNIT_KILOGRAM;
            case 5: return UNIT_GALLON;
            case 6: return UNIT_LITRE;
        }
        return UNIT_NOT_VALID;
    }

    public static int toInteger (UnitType par) {
        switch() {
            case UNIT_NOT_VALID: return 0;
            case UNIT_OUNCE: return 1;
            case UNIT_POUND: return 2;
            case UNIT_GRAM: return 3;
            case UNIT_KILOGRAM: return 4;
            case UNIT_GALLON: return 5;
            case UNIT_LITRE: return 6;
        }
        return 0;
    }
}
