package be.stockandshopbackend.dl.enums;

import lombok.Getter;

@Getter
public enum Unity {
    OTHER("other"),
    PIECE("piece"),
    MILLILITER("milliliter"),
    GRAMS("grams"),
    JAR("jar"),
    BOTTLE("bottle"),
    PACKET("packet"),
    BOX("box"),
    CAN("can"),
    TIN("tin");

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

    /** Lenient parse used for optional chosen units: null/blank/unknown -> null (fall back to product default). */
    public static Unity fromValueOrNull(String value) {
        if (value == null || value.isBlank()) return null;
        for (Unity u : Unity.values()) {
            if (u.getValue().equalsIgnoreCase(value.trim()) || u.name().equalsIgnoreCase(value.trim())) {
                return u;
            }
        }
        return null;
    }
}
