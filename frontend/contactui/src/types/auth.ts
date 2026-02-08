export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  userId: string;
  jwt: string;
}

export interface SignupRequest {
  email: string;
  phone: string;
  firstname: string;
  lastname: string;
  password: string;
}

export interface SignupResponseDto {
  userId: string;
  username: string;
}
