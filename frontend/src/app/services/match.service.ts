import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface MatchDto {
  id?: number;
  nomEquipe: string;
  points: number;
}

@Injectable({ providedIn: 'root' })
export class MatchService {
  private base = `${environment.planningServiceUrl}/matches`;
  constructor(private http: HttpClient) {}

  list(): Observable<{ success: boolean; data: MatchDto[] }> {
    return this.http.get<{ success: boolean; data: MatchDto[] }>(this.base);
    }

  create(body: MatchDto): Observable<{ success: boolean; data: MatchDto }> {
    return this.http.post<{ success: boolean; data: MatchDto }>(this.base, body);
  }

  update(id: number, body: MatchDto): Observable<{ success: boolean; data: MatchDto }> {
    return this.http.put<{ success: boolean; data: MatchDto }>(`${this.base}/${id}`, body);
  }

  remove(id: number): Observable<{ success: boolean }> {
    return this.http.delete<{ success: boolean }>(`${this.base}/${id}`);
  }

  increment(id: number, delta: number): Observable<{ success: boolean; data: MatchDto }> {
    return this.http.patch<{ success: boolean; data: MatchDto }>(`${this.base}/${id}/points?delta=${delta}`, {});
  }
}
