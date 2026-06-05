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

export interface PagedRecipeResponse {
  recipes: Recipe[];
  total: number;
  page: number;
  size: number;
  hasMore: boolean;
}
