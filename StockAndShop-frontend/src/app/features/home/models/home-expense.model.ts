export interface HomeExpense {
  id: number,
  name: string,
  amount: number,
  userConcernedName: string[],
  payerName: string
}

export interface HomeExpenseRequest {
  name: string;
  amount: number;
  homeId: string;
  userConcernedId: string[];
  payerId: string;
}
