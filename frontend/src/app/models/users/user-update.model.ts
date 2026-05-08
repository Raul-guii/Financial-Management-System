import { Role } from './role.enum';

export interface UserUpdateRequest {
  name?: string;
  email?: string;
  password?: string;
  role: Role;
}