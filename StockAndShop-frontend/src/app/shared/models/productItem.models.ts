export interface ProductItem {
  id: number,
  nameProduct: string,
  quantity: number,
  unityProduct: string,
  isChecked: boolean
}

export interface ProductItemRequest {
  name: string,
  quantity: number,
}
