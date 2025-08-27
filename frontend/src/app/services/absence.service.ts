import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CreateAbsenceRequestDto {
  playerId?: number | null;
  email?: string | null;
  dateDebut: string; // ISO LocalDate YYYY-MM-DD
  dateFin?: string | null; // ISO LocalDate
  typeAbsence: 'MALADIE' | 'BLESSURE' | 'PERSONNEL' | 'PROFESSIONNEL' | 'VACANCES' | 'AUTRE';
  raison?: string | null;
  justifiee?: boolean | null;
  commentaires?: string | null;
}

export interface PlayerSummaryDto {
  id: number;
  nom: string;
  prenom: string;
  position?: string;
}

export interface AbsenceDto {
  id: number;
  player: PlayerSummaryDto | null;
  dateDebut: string; // YYYY-MM-DD
  dateFin?: string | null;
  typeAbsence: 'MALADIE' | 'BLESSURE' | 'PERSONNEL' | 'PROFESSIONNEL' | 'VACANCES' | 'AUTRE';
  raison?: string | null;
  statut: 'EN_ATTENTE' | 'APPROUVEE' | 'REFUSEE' | 'ANNULEE';
  justifiee: boolean;
  commentaires?: string | null;
}

@Injectable({ providedIn: 'root' })
export class AbsenceApiService {
  private base = `${environment.performanceServiceUrl}/absences`;

  constructor(private http: HttpClient) {}

  create(req: CreateAbsenceRequestDto): Observable<any> {
    return this.http.post<any>(`${this.base}`, req);
  }

  getAll(): Observable<{ success: boolean; data: AbsenceDto[]; count: number }> {
    return this.http.get<{ success: boolean; data: AbsenceDto[]; count: number }>(`${this.base}`);
  }

  approve(id: number, commentaires?: string | null): Observable<any> {
    const params: any = {};
    if (commentaires) params.commentaires = commentaires;
    return this.http.patch<any>(`${this.base}/${id}/approve`, {}, { params });
  }

  refuse(id: number, commentaires?: string | null): Observable<any> {
    const params: any = {};
    if (commentaires) params.commentaires = commentaires;
    return this.http.patch<any>(`${this.base}/${id}/refuse`, {}, { params });
  }

  cancel(id: number, commentaires?: string | null): Observable<any> {
    const params: any = {};
    if (commentaires) params.commentaires = commentaires;
    return this.http.patch<any>(`${this.base}/${id}/cancel`, {}, { params });
  }
  
}
