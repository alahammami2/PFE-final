import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, map, Observable, of } from 'rxjs';
import { environment } from '../../../../environments/environment';

interface ApiResponse<T> { success?: boolean; message?: string; data?: T }

export interface EventItem {
  id?: number;
  titre?: string;
  lieu?: string;
  dateDebut?: string; // ISO
  dateFin?: string;   // ISO
  type?: string;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  // Reactive budget stream to allow live updates across the app
  private _teamBudget$ = new BehaviorSubject<number | null>(null);
  teamBudget$ = this._teamBudget$.asObservable();

  // Prochain événement: prend le premier de /events/upcoming si dispo
  getNextEvent(): Observable<EventItem | null> {
    const url = `${environment.planningServiceUrl}/events/upcoming`;
    return this.http.get<ApiResponse<EventItem[]> | EventItem[]>(url).pipe(
      map((res: any) => {
        const list: EventItem[] = Array.isArray(res) ? res : (res?.data ?? []);
        if (!list || list.length === 0) return null;
        const now = Date.now();
        // garder seulement les événements futurs (>= maintenant)
        const future = list.filter(e => {
          const t = e?.dateDebut ? Date.parse(e.dateDebut) : NaN;
          return !isNaN(t) && t >= now;
        });
        if (future.length === 0) return null;
        // choisir celui avec le plus petit delta positif
        let best: EventItem | null = null;
        let bestDelta = Number.POSITIVE_INFINITY;
        for (const e of future) {
          const t = Date.parse(e.dateDebut!);
          const delta = t - now;
          if (delta >= 0 && delta < bestDelta) {
            best = e;
            bestDelta = delta;
          }
        }
        return best;
      })
    );
  }

  // Budget total de l'équipe
  getTeamTotalBudget(): Observable<number> {
    const url = `${environment.financeServiceUrl}/budgets/total`;
    return this.http.get<number>(url).pipe(
      map((v) => (typeof v === 'number' ? v : Number(v) || 0))
    );
  }

  // Refresh and push latest team budget into the stream
  refreshTeamTotalBudget(): void {
    this.getTeamTotalBudget().subscribe({
      next: (v) => this._teamBudget$.next(v),
      error: () => this._teamBudget$.next(0)
    });
  }

  // Nombre total de fichiers de performance (utilisé comme proxy pour matchs joués)
  getPerformanceFileCount(): Observable<number> {
    const url = `${environment.performanceServiceUrl}/files/count`;
    return this.http.get<any>(url).pipe(
      map((res: any) => {
        if (typeof res === 'number') return res; // fallback if gateway returns primitive
        const v = res?.count ?? res?.data;
        return typeof v === 'number' ? v : Number(v) || 0;
      })
    );
  }

  // Nombre de matchs joués (passés) depuis le service planning
  getPastMatchesCount(): Observable<number> {
    const url = `${environment.planningServiceUrl}/events/matches/past/count`;
    return this.http.get<any>(url).pipe(
      map((res: any) => {
        if (typeof res === 'number') return res;
        const v = res?.count ?? res?.total ?? res?.data?.count ?? res?.data?.total ?? res?.data;
        return typeof v === 'number' ? v : Number(v) || 0;
      })
    );
  }

  // Nombre d'utilisateurs: compte /api/auth/users
  getUserCount(): Observable<number> {
    const url = `${environment.authServiceUrl}/users/count`;
    return this.http.get<any>(url).pipe(
      map((res: any) => {
        // ApiResponse { data: number } ou directement number
        if (typeof res === 'number') return res;
        const v = res?.data;
        return typeof v === 'number' ? v : Number(v) || 0;
      })
    );
  }
}
