import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

// Backend-aligned DTOs
export type MedicalStatus = 'EN_SUIVI' | 'REPOS' | 'RETABLI';

export interface HealthRecordDto {
  id: number;
  playerId: number;
  playerName: string;
  blessureType?: string | null;
  blessureDate?: string | null; // ISO date string yyyy-MM-dd
  statutPhysique?: string | null; // zone/description
  status: MedicalStatus;
  lastMedicalCheckup?: string | null;
  nextCheckupDue?: string | null;
  createdAt?: string;
  updatedAt?: string | null;
}

export interface CreateHealthRecordInput {
  playerId: number;
  playerName: string;
  blessureType?: string | null;
  blessureDate?: string | null; // yyyy-MM-dd
  statutPhysique?: string | null;
}

export interface UpdateHealthRecordInput {
  playerId?: number;
  playerName?: string;
  blessureType?: string | null;
  blessureDate?: string | null; // yyyy-MM-dd
  statutPhysique?: string | null;
  status?: MedicalStatus;
}

export interface MedicalRendezvousDto {
  id: number;
  playerId: number;
  playerName: string;
  rendezvousDatetime: string; // ISO datetime yyyy-MM-ddTHH:mm:ss
  kineName?: string | null;
  lieu?: string | null;
  priority?: string | null; // Normale/Haute/Urgente (free text backend)
  status?: string | null; // PENDING/CONFIRMED/CANCELLED/COMPLETED
  notes?: string | null;
  createdAt?: string;
  updatedAt?: string | null;
}

export interface CreateRendezvousInput {
  playerId: number;
  playerName: string;
  rendezvousDatetime: string; // ISO datetime
  lieu?: string | null;
  priority?: string | null;
  notes?: string | null;
}

@Injectable({ providedIn: 'root' })
export class MedicalApiService {
  private base = environment.medicalServiceUrl;
  constructor(private http: HttpClient) {}

  // Health Records
  getHealthRecords(): Observable<HealthRecordDto[]> {
    return this.http.get<HealthRecordDto[]>(`${this.base}/dossiers-sante`);
    }

  createHealthRecord(input: CreateHealthRecordInput): Observable<HealthRecordDto> {
    return this.http.post<HealthRecordDto>(`${this.base}/dossiers-sante`, input);
  }

  updateHealthRecord(id: number, input: UpdateHealthRecordInput): Observable<HealthRecordDto> {
    return this.http.put<HealthRecordDto>(`${this.base}/dossiers-sante/${id}`, input);
  }

  setHealthStatus(id: number, status: MedicalStatus): Observable<HealthRecordDto> {
    const params = new HttpParams().set('status', status);
    return this.http.put<HealthRecordDto>(`${this.base}/dossiers-sante/${id}/status`, null, { params });
  }

  // Rendezvous
  listRendezvous(): Observable<MedicalRendezvousDto[]> {
    return this.http.get<MedicalRendezvousDto[]>(`${this.base}/rendezvous`);
  }

  createRendezvous(input: CreateRendezvousInput): Observable<MedicalRendezvousDto> {
    return this.http.post<MedicalRendezvousDto>(`${this.base}/rendezvous`, input);
  }

  updateRendezvous(id: number, input: Partial<MedicalRendezvousDto>): Observable<MedicalRendezvousDto> {
    return this.http.put<MedicalRendezvousDto>(`${this.base}/rendezvous/${id}`, input);
  }

  deleteRendezvous(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/rendezvous/${id}`);
  }

  confirmRendezvous(id: number): Observable<MedicalRendezvousDto> {
    return this.http.put<MedicalRendezvousDto>(`${this.base}/rendezvous/${id}/confirmer`, {});
  }
}
