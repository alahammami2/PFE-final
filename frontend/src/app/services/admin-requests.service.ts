import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

// Backend enums as string literals
export type RequestStatus =
  | 'BROUILLON'
  | 'SOUMISE'
  | 'EN_COURS'
  | 'EN_ATTENTE'
  | 'APPROUVEE'
  | 'REJETEE'
  | 'ANNULEE'
  | 'TERMINEE';

export type RequestType =
  | 'CONGE'
  | 'ABSENCE'
  | 'MATERIEL'
  | 'TRANSPORT'
  | 'HEBERGEMENT'
  | 'BUDGET'
  | 'FORMATION'
  | 'EVENEMENT'
  | 'PARTENARIAT'
  | 'AUTRE';

export type RequestPriority = 'BASSE' | 'NORMALE' | 'HAUTE' | 'URGENTE' | 'CRITIQUE';

// DTOs aligned with backend AdminRequest entity
export interface AdminRequestDto {
  id: number;
  requesterId: number;
  description: string;
  type: RequestType;
  priority: RequestPriority;
  status: RequestStatus;
  budgetRequested?: number | null;
  createdAt: string; // ISO string from backend
}

export interface CreateAdminRequestDto {
  description?: string; // optional in UI; will default to "-" if empty
  type: RequestType;
  priority: RequestPriority;
  budgetRequested?: number | null;
}

@Injectable({ providedIn: 'root' })
export class AdminRequestsApiService {
  private readonly baseUrl = environment.adminRequestServiceUrl; // http://localhost:8090/api/admin-requests
  
  constructor(private http: HttpClient, private auth: AuthService) {}

  // GET all requests
  getAll(): Observable<AdminRequestDto[]> {
    return this.http.get<AdminRequestDto[]>(`${this.baseUrl}`);
  }

  // POST create a new request (status will be BROUILLON by backend)
  create(input: CreateAdminRequestDto): Observable<AdminRequestDto> {
    const userId = this.auth.getCurrentUser()?.id ?? 0;
    const payload: Partial<AdminRequestDto> = {
      requesterId: userId,
      description: (input.description && input.description.trim()) ? input.description.trim() : '-',
      type: input.type,
      priority: input.priority,
      budgetRequested: this.normalizeBudget(input.budgetRequested)
    } as any;

    return this.http.post<AdminRequestDto>(`${this.baseUrl}`, payload);
  }

  // PUT update non-status fields
  update(id: number, input: Partial<CreateAdminRequestDto>): Observable<AdminRequestDto> {
    const body: any = {
      description: input.description ?? undefined,
      type: input.type ?? undefined,
      priority: input.priority ?? undefined,
      budgetRequested: this.normalizeBudget(input.budgetRequested)
    };
    return this.http.put<AdminRequestDto>(`${this.baseUrl}/update`, body, {
      params: new HttpParams().set('id', String(id))
    });
  }

  // PUT set arbitrary status (EN_COURS, REJETEE, etc.)
  setStatus(id: number, status: RequestStatus): Observable<AdminRequestDto> {
    return this.http.put<AdminRequestDto>(`${this.baseUrl}/set-status`, null, {
      params: new HttpParams().set('id', String(id)).set('status', status)
    });
  }

  // Existing business endpoints
  submit(id: number): Observable<AdminRequestDto> {
    return this.http.put<AdminRequestDto>(`${this.baseUrl}/submit`, null, {
      params: new HttpParams().set('id', String(id))
    });
  }

  cancel(id: number): Observable<AdminRequestDto> {
    return this.http.put<AdminRequestDto>(`${this.baseUrl}/cancel`, null, {
      params: new HttpParams().set('id', String(id))
    });
  }

  complete(id: number): Observable<AdminRequestDto> {
    return this.http.put<AdminRequestDto>(`${this.baseUrl}/complete`, null, {
      params: new HttpParams().set('id', String(id))
    });
  }

  // Helpers
  private normalizeBudget(value?: number | null): number | null {
    if (value === undefined || value === null) return null;
    const n = Number(value);
    if (Number.isNaN(n) || !Number.isFinite(n)) return null;
    return Math.max(0, Math.round(n * 100) / 100); // enforce >= 0 and 2 decimals
  }
}
