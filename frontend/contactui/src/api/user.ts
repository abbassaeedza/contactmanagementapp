import { apiGet, apiPut, apiDelete } from './client';
import type {
  UserResponse,
  UserRequest,
  ChangePassRequest,
} from '../types/user';

export function getUser() {
  return apiGet<UserResponse>('/user');
}

export function updateUser(body: UserRequest) {
  return apiPut<UserResponse>('/user/edit', body, { skip401Logout: true });
}

export function changePassword(body: ChangePassRequest) {
  return apiPut<unknown>('/user/change-password', body, {
    skip401Logout: true,
  });
}

export function deleteUser() {
  return apiDelete('/user');
}
