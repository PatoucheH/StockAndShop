export interface RecipeIngredient {
  productId: number;
  productName: string;
  quantity: number;
  unity: string;
}

export interface Recipe {
  id: string;
  title: string;
  ingredients: RecipeIngredient[];
  steps: string[];
  createdAt: string;
}
