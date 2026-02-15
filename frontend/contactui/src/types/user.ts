export interface UserResponse {
  id: string;
  username: string;
  firstname: string;
  lastname: string;
}

export interface UserRequest {
  email: string;
  phone: string;
  firstname: string;
  lastname: string;
}

export interface ChangePassRequest {
  oldpassword: string;
  newpassword: string;
}
