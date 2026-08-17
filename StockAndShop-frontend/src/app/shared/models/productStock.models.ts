export interface ProductStock{
  id: number;
  nameProduct: string;
  quantity: number;
  category: string,
  unityProduct: string,
}

export interface ProductStockDecrese{
  name: string;
  quantity: number;
  unity?: string;
}
