package recipeconverter.org.recipeconverter.dao;

public enum UnitType {
    //ADIMENSIONAL
    NO_DIM,
    //WEIGHT
    UNIT_OUNCE,
    UNIT_POUND,
    UNIT_GRAM,
    UNIT_CENTI_GRAM,
    UNIT_KILOGRAM,
    //CAPACITY
    UNIT_GALLON,
    UNIT_LITRE,
    UNIT_CENTI_LITRE,
    UNIT_MILLI_LITRE,
    //others
    UNIT_NOT_VALID;

    static public int getNumber() {
        return 10;
    }

    static public String toString(UnitType par) {
        switch (par) {
            case NO_DIM: return "#";
            case UNIT_OUNCE: return "oz";
            case UNIT_POUND: return "lb";
            case UNIT_GRAM: return "gr";
            case UNIT_CENTI_GRAM: return "cgr";
            case UNIT_KILOGRAM: return "kg";
            case UNIT_GALLON: return "gal";
            case UNIT_LITRE: return "ltr";
            case UNIT_CENTI_LITRE: return "cl";
            case UNIT_MILLI_LITRE: return "ml";
        }
        return "";
    }

    public static UnitType fromInteger (int par) {
        switch(par) {
            case 0: return NO_DIM;
            case 1: return UNIT_OUNCE;
            case 2: return UNIT_POUND;
            case 3: return UNIT_GRAM;
            case 4: return UNIT_CENTI_GRAM;
            case 5: return UNIT_KILOGRAM;
            case 6: return UNIT_GALLON;
            case 7: return UNIT_LITRE;
            case 8: return UNIT_CENTI_LITRE;
            case 9: return UNIT_MILLI_LITRE;
        }
        return UNIT_NOT_VALID;
    }

    public static int toInteger (UnitType par) {
        switch(par) {
            case NO_DIM: return 0;
            case UNIT_OUNCE: return 1;
            case UNIT_POUND: return 2;
            case UNIT_GRAM: return 3;
            case UNIT_CENTI_GRAM: return 4;
            case UNIT_KILOGRAM: return 5;
            case UNIT_GALLON: return 6;
            case UNIT_LITRE: return 7;
            case UNIT_CENTI_LITRE: return 8;
            case UNIT_MILLI_LITRE: return 9;
        }
        return -1;
    }
}
