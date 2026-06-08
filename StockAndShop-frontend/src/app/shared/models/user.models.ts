export interface User{
  id: string,
  name: string,
  email: string,
  homeRole: string | null
}

export interface UserSearchResult {
  id: string,
  name: string,
  email: string,
}
