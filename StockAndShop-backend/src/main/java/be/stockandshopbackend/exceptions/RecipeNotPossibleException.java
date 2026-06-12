package be.stockandshopbackend.exceptions;

public class RecipeNotPossibleException extends RuntimeException {
    public RecipeNotPossibleException(String message) {
        super(message);
    }
}
