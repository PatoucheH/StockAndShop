export interface ProductItem {
  id: string,
  nameProduct: string,
  quantity: number,
  unityProduct: string,
  isChecked: boolean
}

export interface ProductItemRequest {
  name: string,
  quantity: number,
}
