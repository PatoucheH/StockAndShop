export interface Product{
  id: number,
  name: string,
  unity: string,
  category: string,
  barcode?: string,
  brand?: string,
  imageUrl?: string,
  packageQuantity?: string,
  nutriscoreGrade?: string,
  ecoscoreGrade?: string,
}

export interface ProductRequest {
  name: string,
  unity: string,
  category: string,
}
