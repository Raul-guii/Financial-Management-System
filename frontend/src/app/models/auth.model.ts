export interface LoginResponse {
  token: string;
  type: string;
  userId: number;
  name: string;
  email: string;
  role: string; 
}