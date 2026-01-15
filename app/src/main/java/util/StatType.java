package util;


public enum StatType {
    ANIMAL_COUNT,
    PLANT_COUNT,
    FREE_FIELDS,
    AVG_ENERGY,
    AVG_LIFESPAN,
    AVG_CHILDREN;

    @Override
    public String toString() {
        return switch (this) {
            case ANIMAL_COUNT -> "Liczba zwierząt";
            case PLANT_COUNT -> "Liczba roślin";
            case FREE_FIELDS -> "Wolne pola";
            case AVG_ENERGY -> "Średnia energia";
            case AVG_LIFESPAN -> "Średnia długość życia";
            case AVG_CHILDREN -> "Średnia liczba dzieci";
        };
    }
}