const AUTH_TOKEN_KEY = 'auth_token';

export function getToken(): string | null {
  return localStorage.getItem(AUTH_TOKEN_KEY);
}

export function setToken(jwt: string): void {
  localStorage.setItem(AUTH_TOKEN_KEY, jwt);
}

export function clearToken(): void {
  localStorage.removeItem(AUTH_TOKEN_KEY);
}

export function isAuthenticated(): boolean {
  return !!getToken();
}
