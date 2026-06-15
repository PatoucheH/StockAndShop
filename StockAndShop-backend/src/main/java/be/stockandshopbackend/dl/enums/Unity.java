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
    CAN("can");

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
