package be.stockandshopbackend.dl.entities.product;

import be.stockandshopbackend.dl.enums.Unity;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores a product's list of possible units as a comma-separated string, e.g. "BOTTLE,CAN".
 * The first element is treated as the default unit.
 */
@Converter
public class UnityListConverter implements AttributeConverter<List<Unity>, String> {

    @Override
    public String convertToDatabaseColumn(List<Unity> attribute) {
        if (attribute == null || attribute.isEmpty()) return null;
        return attribute.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public List<Unity> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return new ArrayList<>();
        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Unity::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
