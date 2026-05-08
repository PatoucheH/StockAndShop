import { ProductItem } from '../../shared/models/productItem.models';

export interface ShoppingList {
  id: number,
  name: string,
  description: string,
  products: ProductItem[]
}

export interface ShoppingListRequest {
  name: string,
  description: string,
}
