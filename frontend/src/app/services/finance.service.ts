import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';

export interface DepenseDto {
  id: number;
  montant: number;
  date: string; // YYYY-MM-DD
  description?: string;
  statut?: 'PAYEE' | 'EN_ATTENTE' | 'REJETE' | string;
  categorie?: string;
}

export interface RecetteDto {
  id: number;
  montant: number;
  date: string; // YYYY-MM-DD
  description?: string;
  categorie?: string;
}

export interface BudgetDto {
  id?: number;
  montant: number;
}

@Injectable({ providedIn: 'root' })
export class FinanceApiService {
  private base = environment.financeServiceUrl;
  constructor(private http: HttpClient) {}

  // GET /depenses -> ApiResponse { data: DepenseDto[] } or raw array
  getDepenses(): Observable<DepenseDto[]> {
    const url = `${this.base}/depenses`;
    return this.http.get<any>(url).pipe(
      tap({ next: (res) => console.log('[FinanceApi] GET', url, '->', res), error: (e) => console.error('[FinanceApi] GET', url, 'ERROR', e) }),
      map((res: any) => {
        const list: any[] = Array.isArray(res) ? res : (res?.data ?? []);
        return (list || []) as DepenseDto[];
      })
    );
  }

  // POST /depenses
  createDepense(payload: Omit<DepenseDto, 'id'>): Observable<DepenseDto> {
    const url = `${this.base}/depenses`;
    return this.http.post<any>(url, payload).pipe(
      tap({ next: (res) => console.log('[FinanceApi] POST', url, 'payload=', payload, '->', res), error: (e) => console.error('[FinanceApi] POST', url, 'payload=', payload, 'ERROR', e) }),
      map((res: any) => (res?.data ?? res) as DepenseDto)
    );
  }

  // PUT /depenses/{id}
  updateDepense(id: number, payload: Omit<DepenseDto, 'id'>): Observable<DepenseDto> {
    const url = `${this.base}/depenses/${id}`;
    return this.http.put<any>(url, payload).pipe(
      map((res: any) => (res?.data ?? res) as DepenseDto)
    );
  }

  // GET /categories-budget -> recettes
  getRecettes(): Observable<RecetteDto[]> {
    const url = `${this.base}/categories-budget`;
    return this.http.get<any>(url).pipe(
      tap({ next: (res) => console.log('[FinanceApi] GET', url, '->', res), error: (e) => console.error('[FinanceApi] GET', url, 'ERROR', e) }),
      map((res: any) => {
        const list: any[] = Array.isArray(res) ? res : (res?.data ?? []);
        return (list || []) as RecetteDto[];
      })
    );
  }

  // POST /categories-budget
  createRecette(payload: Omit<RecetteDto, 'id'>): Observable<RecetteDto> {
    const url = `${this.base}/categories-budget`;
    return this.http.post<any>(url, payload).pipe(
      tap({ next: (res) => console.log('[FinanceApi] POST', url, 'payload=', payload, '->', res), error: (e) => console.error('[FinanceApi] POST', url, 'payload=', payload, 'ERROR', e) }),
      map((res: any) => (res?.data ?? res) as RecetteDto)
    );
  }

  // PUT /categories-budget/{id}
  updateRecette(id: number, payload: Omit<RecetteDto, 'id'>): Observable<RecetteDto> {
    const url = `${this.base}/categories-budget/${id}`;
    return this.http.put<any>(url, payload).pipe(
      map((res: any) => (res?.data ?? res) as RecetteDto)
    );
  }

  // POST /budgets/record-total?montant=... -> enregistre le montant total dans la table budgets
  recordTotal(montant: number): Observable<any> {
    const url = `${this.base}/budgets/record-total?montant=${encodeURIComponent(String(montant ?? 0))}`;
    return this.http.post<any>(url, null).pipe(
      tap({ next: (res) => console.log('[FinanceApi] POST', url, '->', res), error: (e) => console.error('[FinanceApi] POST', url, 'ERROR', e) })
    );
  }

  updateBudget(id: number, montant: number): Observable<BudgetDto> {
    const url = `${this.base}/budgets/${id}`;
    const payload: BudgetDto = { montant: Number(montant ?? 0) };
    return this.http.put<BudgetDto>(url, payload).pipe(
      tap({
        next: (res) => console.log('[FinanceApi] PUT', url, 'payload=', payload, '->', res),
        error: (e) => console.error('[FinanceApi] PUT', url, 'payload=', payload, 'ERROR', e)
      })
      
    );
  }
}
