export interface ProductItem {
  id: string,
  nameProduct: string,
  quantity: number,
  unityProduct: string,
  isChecked: boolean
}

export interface ProductItemRequest {
  nameProduct: string,
  quantity: number,
}
