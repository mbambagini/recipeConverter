package recipeconverter.org.recipeconverter.dao;

public enum UnitType {
    //ADIMENSIONAL
    NO_DIM,
    //WEIGHT
    UNIT_OUNCE,
    UNIT_POUND,
    UNIT_GRAM,
    UNIT_KILOGRAM,
    //CAPACITY
    UNIT_GALLON,
    UNIT_LITRE,
    UNIT_CENTI_LITRE,
    UNIT_MILLI_LITRE,
    //SIZE
    UNIT_INCH,
    UNIT_CENTIMETER,
    UNIT_METER,
    //others
    UNIT_PERCENTAGE,

    UNIT_NOT_VALID;

    static public int getNumber() {
        return 13;
    }

    static public String toString(UnitType par) {
        switch (par) {
            case NO_DIM: return "#";
            case UNIT_OUNCE: return "oz";
            case UNIT_POUND: return "lb";
            case UNIT_GRAM: return "gr";
            case UNIT_KILOGRAM: return "kg";
            case UNIT_GALLON: return "gal";
            case UNIT_LITRE: return "ltr";
            case UNIT_CENTI_LITRE: return "cl";
            case UNIT_MILLI_LITRE: return "ml";
            case UNIT_INCH: return "in";
            case UNIT_CENTIMETER: return "cm";
            case UNIT_METER: return "m";
            case UNIT_PERCENTAGE: return "%";
        }
        return "";
    }

    public static UnitType fromInteger (int par) {
        switch(par) {
            case 0: return NO_DIM;
            case 1: return UNIT_OUNCE;
            case 2: return UNIT_POUND;
            case 3: return UNIT_GRAM;
            case 4: return UNIT_KILOGRAM;
            case 5: return UNIT_GALLON;
            case 6: return UNIT_LITRE;
            case 7: return UNIT_CENTI_LITRE;
            case 8: return UNIT_MILLI_LITRE;
            case 9: return UNIT_INCH;
            case 10: return UNIT_CENTIMETER;
            case 11: return UNIT_METER;
            case 12: return UNIT_PERCENTAGE;
        }
        return UNIT_NOT_VALID;
    }

    public static int toInteger (UnitType par) {
        switch(par) {
            case NO_DIM: return 0;
            case UNIT_OUNCE: return 1;
            case UNIT_POUND: return 2;
            case UNIT_GRAM: return 3;
            case UNIT_KILOGRAM: return 4;
            case UNIT_GALLON: return 5;
            case UNIT_LITRE: return 6;
            case UNIT_CENTI_LITRE: return 7;
            case UNIT_MILLI_LITRE: return 8;
            case UNIT_INCH: return 9;
            case UNIT_CENTIMETER: return 10;
            case UNIT_METER: return 11;
            case UNIT_PERCENTAGE: return 12;
        }
        return -1;
    }
}
