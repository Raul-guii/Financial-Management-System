import { Role } from './role.enum';

export interface UserCreateRequest {
  name: string;
  email: string;
  password: string;
  role: Role;
}