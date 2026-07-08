export interface HomeExpense {
  id: number,
  name: string,
  amount: number,
  userConcernedName: string[],
  payerName: string,
  createdAt: string
}

export interface PagedHomeExpenseResponse {
  expenses: HomeExpense[];
  total: number;
  page: number;
  size: number;
  hasMore: boolean;
}

export interface HomeExpenseRequest {
  name: string;
  amount: number;
  homeId: string;
  userConcernedId: string[];
  payerId: string;
}
