import { Role } from './user.model';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
  role: Role;
}

export interface AuthResponse {
  id: number;
  name: string;
  email: string;
  role: Role;
  message: string;
}
