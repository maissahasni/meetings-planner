export enum Role {
  ADMIN = 'ADMIN',
  USER = 'USER'
}

export interface User {
  id?: number;
  name: string;
  email: string;
  role: Role;
}

export interface UserRequest {
  name: string;
  email: string;
  password: string;
  role: Role;
}
