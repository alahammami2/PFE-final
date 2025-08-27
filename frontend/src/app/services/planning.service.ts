import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

// Backend enum string values
export type PlanningEventType = 'ENTRAINEMENT' | 'MATCH_AMICAL' | 'CHAMPIONNAT' | 'COUPE' | 'REUNION' | 'VISONNAGE' | 'AUTRE';

export interface CreateEventRequest {
  titre: string;
  description?: string;
  dateDebut: string; // ISO LocalDateTime: YYYY-MM-DDTHH:mm:ss
  dateFin: string;   // ISO LocalDateTime: YYYY-MM-DDTHH:mm:ss
  type: PlanningEventType;
  lieu?: string;
}

export interface UpdateEventRequest extends CreateEventRequest {}

export interface EventResponse {
  id: number;
  titre: string;
  description?: string;
  dateDebut: string; // ISO
  dateFin: string;   // ISO
  type: PlanningEventType;
  lieu?: string;
  actif?: boolean;
  dateCreation?: string;
  dateModification?: string;
}

@Injectable({ providedIn: 'root' })
export class PlanningApiService {
  private base = environment.planningServiceUrl;
  constructor(private http: HttpClient) {}

  getAll(): Observable<EventResponse[]> {
    return this.http.get<any>(`${this.base}/events`).pipe(
      // API shape: { success, message, data }
      // eslint-disable-next-line @typescript-eslint/no-unsafe-return
      (source => new Observable<EventResponse[]>(observer => {
        const sub = source.subscribe({
          next: (res: any) => observer.next((res?.data ?? []) as EventResponse[]),
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }

  getUpcoming(): Observable<EventResponse[]> {
    return this.http.get<any>(`${this.base}/events/upcoming`).pipe(
      (source => new Observable<EventResponse[]>(observer => {
        const sub = source.subscribe({
          next: (res: any) => observer.next((res?.data ?? []) as EventResponse[]),
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }

  create(payload: CreateEventRequest): Observable<EventResponse> {
    return this.http.post<any>(`${this.base}/events`, payload).pipe(
      (source => new Observable<EventResponse>(observer => {
        const sub = source.subscribe({
          next: (res: any) => observer.next(res?.data as EventResponse),
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }

  update(id: number, payload: UpdateEventRequest): Observable<EventResponse> {
    return this.http.put<any>(`${this.base}/events/${id}`, payload).pipe(
      (source => new Observable<EventResponse>(observer => {
        const sub = source.subscribe({
          next: (res: any) => observer.next(res?.data as EventResponse),
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<any>(`${this.base}/events/${id}`).pipe(
      (source => new Observable<void>(observer => {
        const sub = source.subscribe({
          next: () => observer.next(),
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }

  // Matches played (past) count: returns { championnat, coupe, total }
  getPastMatchesCount(): Observable<{ championnat: number; coupe: number; total: number }> {
    return this.http.get<any>(`${this.base}/events/matches/past/count`).pipe(
      (source => new Observable<{ championnat: number; coupe: number; total: number }>(observer => {
        const sub = source.subscribe({
          next: (res: any) => {
            const data = res?.data ?? {};
            observer.next({
              championnat: Number(data.championnat ?? 0),
              coupe: Number(data.coupe ?? 0),
              total: Number(data.total ?? 0)
            });
          },
          error: (err) => observer.error(err),
          complete: () => observer.complete(),
        });
        return () => sub.unsubscribe();
      }))
    );
  }
}
