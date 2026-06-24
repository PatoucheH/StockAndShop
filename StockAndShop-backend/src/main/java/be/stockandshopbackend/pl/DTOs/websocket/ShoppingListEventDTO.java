package be.stockandshopbackend.pl.DTOs.websocket;

/**
 * Event broadcast to every client subscribed to a shopping list WebSocket topic.
 *
 * <p>{@code payload} is untyped because its structure varies per event type —
 * see {@link ShoppingListEventType} for what each value carries.</p>
 *
 * <p>{@code triggeredByUsername} lets the receiving client skip updates it already
 * applied optimistically when it dispatched the action.</p>
 */
public record ShoppingListEventDTO(
        ShoppingListEventType type,
        Long shoppingListId,
        String triggeredByUsername,
        Object payload
) {}
