package be.stockandshopbackend.dl.enums;

import lombok.Getter;

@Getter
public enum Unity {
    OTHER("other"),
    PIECE("piece"),
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

    public static Unity fromValue(String value) {
        for (Unity u : Unity.values()) {
            if (u.getValue().equalsIgnoreCase(value)) {
                return u;
            }
        }
        throw new IllegalArgumentException("Unknown unity: " + value);
    }
}
