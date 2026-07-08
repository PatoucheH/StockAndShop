export interface User{
  id: string,
  name: string,
  email: string,
  homeRole: string | null,
  balance: number
}

export interface UserSearchResult {
  id: string,
  name: string,
  email: string,
}
