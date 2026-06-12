import { ProductItem } from './productItem.models';

export type ShoppingListEventType =
  | 'ITEM_TOGGLED'
  | 'ITEM_ADDED'
  | 'ITEMS_BATCH_ADDED'
  | 'ITEM_REMOVED'
  | 'ITEM_TRANSFERRED';

export interface ItemToggledPayload {
  itemId: number;
  isChecked: boolean;
}

export interface ItemRemovedPayload {
  itemId: number;
}

export interface ShoppingListWsEvent {
  type: ShoppingListEventType;
  shoppingListId: number;
  triggeredByUsername: string;
  payload: ItemToggledPayload | ItemRemovedPayload | ProductItem | ProductItem[] | null;
}
