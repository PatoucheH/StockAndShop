package be.stockandshopbackend.exceptions;

public class PremiumRequiredException extends RuntimeException {
    public PremiumRequiredException(String message) {
        super(message);
    }
}
