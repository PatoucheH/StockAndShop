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
  timing: number;
  score: number | null;
  tags: string[];
  createdAt: string;
}

export interface PagedRecipeResponse {
  recipes: Recipe[];
  total: number;
  page: number;
  size: number;
  hasMore: boolean;
}

export interface RecipeComment {
  id: number;
  comment: string;
  score: number;
  recipeId: string;
  username: string;
  createdAt: string;
}

export interface RecipeCommentRequest {
  comment: string;
  score: number;
  recipeId: string;
}
