import { ProductItem } from '../../shared/models/productItem.models';

export interface ShoppingList {
  id: number,
  name: string,
  description: string,
  products: ProductItem[],
  homeId: string,
}

export interface ShoppingListRequest {
  name: string,
  description: string,
}
