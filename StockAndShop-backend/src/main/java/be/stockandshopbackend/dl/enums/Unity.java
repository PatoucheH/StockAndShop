package be.stockandshopbackend.dl.enums;

public enum Unity {
    MILLILITERS("milliliters"),
    LITER("liter"),
    GRAMS("grams"),
    KILOGRAMS("kilograms"),
    JAR("jar"),
    TUB("tub"),
    BOTTLE("bottle"),
    PACKET("packet"),
    BOX("box");

    private final String value;

    Unity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Unity fromValue(String value) {
        for (Unity u : Unity.values()) {
            if (u.getValue().equalsIgnoreCase(value)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unknown unity: " + value);
    }
}
