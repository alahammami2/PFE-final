import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

interface AverageNoteResponse {
  success: boolean;
  averageNote: number | null;
  averagePercentage: number | null;
}

export type PositionEnum = 'PASSEUR' | 'ATTAQUANT' | 'CENTRAL' | 'LIBERO' | 'POINTU' | 'RECEPTEUR_ATTAQUANT';

export interface CreatePlayerRequest {
  nom: string;
  prenom: string;
  email?: string;
  dateNaissance: string; // ISO yyyy-mm-dd
  position: PositionEnum;
  numeroMaillot?: number;
  tailleCm?: number;
  poidsKg?: number;
  salaire?: number;
  statut?: 'ACTIF' | 'INACTIF';
}

export interface Player {
  id: number;
  nom: string;
  prenom: string;
  email?: string;
  dateNaissance: string; // ISO yyyy-mm-dd
  position: PositionEnum;
  numeroMaillot?: number;
  tailleCm?: number;
  poidsKg?: number;
  salaire?: number;
  statut: 'ACTIF' | 'BLESSE' | 'SUSPENDU' | 'INACTIF' | 'TRANSFERE';
}

@Injectable({ providedIn: 'root' })
export class PerformanceService {
  private baseUrl = environment.performanceServiceUrl;

  constructor(private http: HttpClient) {}

  getAverageNotePercentage(): Observable<number> {
    return this.http
      .get<AverageNoteResponse>(`${this.baseUrl}/performances/average-note`)
      .pipe(map((res) => (res?.averagePercentage ?? 0)));
  }

  createPlayer(payload: CreatePlayerRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/players`, payload);
  }

  getAllPlayers(): Observable<Player[]> {
    return this.http.get<Player[]>(`${this.baseUrl}/players`);
  }

  deletePlayerByEmail(email: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/players/by-email`, { params: { email } });
  }

  updatePlayer(id: number, payload: CreatePlayerRequest): Observable<Player> {
    return this.http.put<Player>(`${this.baseUrl}/players/${id}`, payload);
  }

  updatePlayerByEmail(email: string, payload: CreatePlayerRequest): Observable<Player> {
    return this.http.put<Player>(`${this.baseUrl}/players/by-email`, payload, { params: { email } });
  }
}
