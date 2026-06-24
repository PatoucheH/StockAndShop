package be.stockandshopbackend.pl.DTOs.websocket;

public enum ShoppingListEventType {
    /** Payload: {@code { itemId, isChecked }}. */
    ITEM_TOGGLED,
    /** Payload: a single {@code ProductItemResponse}. */
    ITEM_ADDED,
    /** Payload: list of {@code ProductItemResponse}. */
    ITEMS_BATCH_ADDED,
    /** Payload: {@code { itemId }}. */
    ITEM_REMOVED,
    /** Payload: {@code null} — clients should reload the full list. */
    ITEM_TRANSFERRED
}
