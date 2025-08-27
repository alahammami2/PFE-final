// Generic API response wrapper from backend
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
}

// Backend AuthResponse payload
export interface AuthResponse {
  token: string;
  type: string; // "Bearer"
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
}

// Login request expected by backend
export interface LoginRequest {
  email: string;
  motDePasse: string;
}

// Angular convenience type for login call
export type LoginResponse = ApiResponse<AuthResponse>;

// Register request expected by backend (create-user)
export interface RegisterRequest {
  nom: string;
  prenom: string;
  email: string;
  motDePasse: string;
  role: string;
  telephone?: string;
  salaire?: number;
}

// Optional current user model stored on client
export interface CurrentUser {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
}

// Backend UserResponse returned by auth-service (list and single user)
export interface UserResponse {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  actif?: boolean;
  dateCreation?: string;
  telephone?: string;
  salaire?: number;
  // Only present in admin-only endpoint /users/with-passwords
  motDePasse?: string;
}

// Returned by POST /api/auth/create-user
export interface CreateUserResult {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  role: string;
  actif?: boolean;
  dateCreation?: string; // ISO string
  telephone?: string;
  salaire?: number;
  // Plaintext password returned ONCE by backend (do not store)
  motDePasseClair?: string;
}

// Reset password feature removed; no DTO needed
