import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { environment } from '../../environments/environment';
import { 
  LoginRequest, 
  LoginResponse, 
  RegisterRequest,
  CurrentUser,
  AuthResponse,
  ApiResponse,
  UserResponse,
  CreateUserResult
} from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private currentUserSubject = new BehaviorSubject<CurrentUser | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  private tokenSubject = new BehaviorSubject<string | null>(this.getTokenFromStorage());
  public token$ = this.tokenSubject.asObservable();

  // Roles derived primarily from JWT, fallback to stored user
  private rolesSubject = new BehaviorSubject<string[]>(this.deriveRoles(this.getTokenFromStorage(), this.getCurrentUserFromStorage()));
  public roles$ = this.rolesSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadUserFromStorage();
    // Ensure roles are initialized from token/user at startup
    this.rolesSubject.next(this.deriveRoles(this.getTokenFromStorage(), this.currentUserSubject.value));
  }

  // Update user by ID
  updateUser(
    userId: number,
    payload: { nom: string; prenom: string; email: string; role: string; actif: boolean; telephone?: string | null; salaire?: number | null }
  ): Observable<ApiResponse<any>> {
    return this.http.put<ApiResponse<any>>(`${environment.authServiceUrl}/users/${userId}`, payload);
  }

  // Delete user by ID
  deleteUser(userId: number): Observable<ApiResponse<any>> {
    return this.http.delete<ApiResponse<any>>(`${environment.authServiceUrl}/users/${userId}`);
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.authServiceUrl}/login`, credentials)
      .pipe(
        tap((response: ApiResponse<AuthResponse>) => {
          const data = response?.data;
          if (data?.token) {
            this.setToken(data.token);
            this.setCurrentUser({
              id: data.id,
              nom: data.nom,
              prenom: data.prenom,
              email: data.email,
              role: data.role
            });
            // Update roles after login
            this.rolesSubject.next(this.deriveRoles(data.token, {
              id: data.id,
              nom: data.nom,
              prenom: data.prenom,
              email: data.email,
              role: data.role
            } as CurrentUser));
          }
        })
      );
  }

  register(userData: RegisterRequest): Observable<ApiResponse<CreateUserResult>> {
    return this.http.post<ApiResponse<CreateUserResult>>(`${environment.authServiceUrl}/create-user`, userData);
  }

  // reset password feature removed

  // Fetch all active users from auth-service
  getUsers(): Observable<UserResponse[]> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${environment.authServiceUrl}/users`).pipe(
      (source => new Observable<UserResponse[]>(observer => {
        const sub = source.subscribe({
          next: (res: ApiResponse<UserResponse[]>) => observer.next((res?.data ?? []) as UserResponse[]),
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }

  // Admin-only: fetch users including motDePasse
  getUsersWithPasswords(): Observable<UserResponse[]> {
    return this.http.get<ApiResponse<UserResponse[]>>(`${environment.authServiceUrl}/users/with-passwords`).pipe(
      (source => new Observable<UserResponse[]>(observer => {
        const sub = source.subscribe({
          next: (res: ApiResponse<UserResponse[]>) => observer.next((res?.data ?? []) as UserResponse[]),
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }

  // Fetch active users count (for dashboard/performance card)
  getActiveUsersCount(): Observable<number> {
    return this.http
      .get<ApiResponse<number>>(`${environment.authServiceUrl}/users/count`)
      .pipe(map((res) => (res && (res as any).data != null ? (res as any).data as number : 0)));
  }

  // Fetch active players count (role = JOUEUR)
  getActivePlayersCount(): Observable<number> {
    return this.http
      .get<ApiResponse<number>>(`${environment.authServiceUrl}/users/count/joueurs`)
      .pipe(map((res) => (res && (res as any).data != null ? (res as any).data as number : 0)));
  }

  logout(): void {
    this.clearAuthData();
    this.currentUserSubject.next(null);
    this.tokenSubject.next(null);
    this.rolesSubject.next([]);
  }

  isAuthenticated(): boolean {
    const token = this.getTokenFromStorage();
    return !!token && !this.isTokenExpired(token);
  }

  getCurrentUser(): CurrentUser | null {
    return this.currentUserSubject.value;
  }

  getToken(): string | null {
    return this.tokenSubject.value;
  }

  // Exposed helpers for guards/directives
  hasRole(role: string): boolean {
    role = this.normalizeRole(role);
    return this.rolesSubject.value.includes(role);
  }

  hasAnyRole(roles: string[]): boolean {
    const want = roles.map((r) => this.normalizeRole(r));
    const have = this.rolesSubject.value;
    return want.some((r) => have.includes(r));
  }

  private setToken(token: string): void {
    localStorage.setItem('auth_token', token);
    this.tokenSubject.next(token);
    // Re-derive roles when token changes
    this.rolesSubject.next(this.deriveRoles(token, this.currentUserSubject.value));
  }

  private setCurrentUser(user: CurrentUser): void {
    localStorage.setItem('current_user', JSON.stringify(user));
    this.currentUserSubject.next(user);
    // If no JWT roles, allow fallback to user.role
    if (this.rolesSubject.value.length === 0) {
      this.rolesSubject.next(this.deriveRoles(this.getTokenFromStorage(), user));
    }
  }

  private getTokenFromStorage(): string | null {
    return localStorage.getItem('auth_token');
  }

  private loadUserFromStorage(): void {
    const userStr = localStorage.getItem('current_user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
        // Refresh roles from stored data
        this.rolesSubject.next(this.deriveRoles(this.getTokenFromStorage(), user));
      } catch (e) {
        console.error('Error parsing user from storage:', e);
        this.clearAuthData();
      }
    }
  }

  private clearAuthData(): void {
    localStorage.removeItem('auth_token');
    localStorage.removeItem('current_user');
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 < Date.now();
    } catch (e) {
      return true;
    }
  }

  private getCurrentUserFromStorage(): CurrentUser | null {
    const str = localStorage.getItem('current_user');
    if (!str) return null;
    try {
      return JSON.parse(str) as CurrentUser;
    } catch {
      return null;
    }
  }

  // ---- Roles helpers ----
  private deriveRoles(token: string | null, user: CurrentUser | null): string[] {
    let roles: string[] = [];
    // Priority: JWT
    const jwtRoles = this.extractRolesFromToken(token);
    if (jwtRoles.length) roles = jwtRoles;
    // Fallback: stored user role
    if (!roles.length && user?.role) roles = [this.normalizeRole(user.role)];
    return Array.from(new Set(roles));
  }

  private extractRolesFromToken(token: string | null): string[] {
    if (!token) return [];
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Aggregate roles from various common providers
      const roles: string[] = [];

      // 1) Simple claims
      const simpleCandidates = [payload?.roles, payload?.role, payload?.authorities, payload?.scope, payload?.scopes];
      for (const cand of simpleCandidates) {
        if (cand == null) continue;
        if (typeof cand === 'string') {
          roles.push(...cand.split(/[ ,]+/).filter(Boolean));
        } else if (Array.isArray(cand)) {
          roles.push(...cand.map((r: any) => String(r)));
        }
      }

      // 2) Keycloak: realm_access.roles
      const realmAccess = payload?.realm_access?.roles;
      if (Array.isArray(realmAccess)) roles.push(...realmAccess.map((r: any) => String(r)));

      // 3) Keycloak: resource_access.{client}.roles
      const resourceAccess = payload?.resource_access;
      if (resourceAccess && typeof resourceAccess === 'object') {
        Object.values(resourceAccess as Record<string, any>).forEach((entry: any) => {
          if (entry?.roles && Array.isArray(entry.roles)) {
            roles.push(...entry.roles.map((r: any) => String(r)));
          }
        });
      }

      // 4) AWS Cognito groups
      const cognitoGroups = payload?.['cognito:groups'];
      if (Array.isArray(cognitoGroups)) roles.push(...cognitoGroups.map((r: any) => String(r)));

      // 5) Generic permissions claim
      const permissions = payload?.permissions;
      if (Array.isArray(permissions)) roles.push(...permissions.map((p: any) => String(p)));

      // Normalize & dedupe
      const normalized = roles.map((r) => this.normalizeRole(r)).filter(Boolean);
      return Array.from(new Set(normalized));
    } catch {
      return [];
    }
  }

  private normalizeRole(role: string): string {
    // Map your domain roles to a standard uppercase without accents/spaces
    const r = role?.toString().trim().toLowerCase();
    if (!r) return '';
    // Known roles per requirement
    const map: Record<string, string> = {
      'admin': 'ADMIN',
      'joueur': 'JOUEUR',
      'coach': 'COACH',
      'invite': 'INVITE',
      'invité': 'INVITE',
      'staf medical': 'STAF_MEDICAL',
      'staf médical': 'STAF_MEDICAL',
      'staf_medical': 'STAF_MEDICAL',
      'staff medical': 'STAF_MEDICAL',
      'staff_medical': 'STAF_MEDICAL',
      'responsable financier': 'RESPONSABLE_FINANCIER',
      'responsable_financier': 'RESPONSABLE_FINANCIER',
      'finance': 'RESPONSABLE_FINANCIER',
    };
    return map[r] || r.toUpperCase();
  }
}
