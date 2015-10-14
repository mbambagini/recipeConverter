package recipeconverter.org.recipeconverter.dao;

public enum UnitType {
    //WEIGHT
    UNIT_OUNCE,
    UNIT_POUND,
    UNIT_GRAM,
    UNIT_KILOGRAM,
    //CAPACITY
    UNIT_GALLON,
    UNIT_LITRE,
    //others
    UNIT_NOT_VALID;

    static public String toString(UnitType par) {
        switch (par) {
            case UNIT_OUNCE:
                return "oz";
            case UNIT_POUND:
                return "lb";
            case UNIT_GRAM:
                return "gr";
            case UNIT_KILOGRAM:
                return "kg";
            case UNIT_GALLON:
                return "gal";
            case UNIT_LITRE:
                return "ltr";
        }
        return "";
    }

    public static UnitType fromInteger (int par) {
        switch(par) {
            case 0: return UNIT_OUNCE;
            case 1: return UNIT_POUND;
            case 2: return UNIT_GRAM;
            case 3: return UNIT_KILOGRAM;
            case 4: return UNIT_GALLON;
            case 5: return UNIT_LITRE;
        }
        return UNIT_NOT_VALID;
    }

    public static int toInteger (UnitType par) {
        switch(par) {
            case UNIT_OUNCE: return 0;
            case UNIT_POUND: return 1;
            case UNIT_GRAM: return 2;
            case UNIT_KILOGRAM: return 3;
            case UNIT_GALLON: return 4;
            case UNIT_LITRE: return 5;
        }
        return -1;
    }
}
