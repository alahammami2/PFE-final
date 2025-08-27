import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PerformanceFileDto } from './performance-file.service';

@Injectable({ providedIn: 'root' })
export class ChatbotGatewayService {
  private base = environment.chatbotGatewayUrl; // e.g., http://localhost:8082/api/chatbot

  constructor(private http: HttpClient) {}

  // Proxy list of files from chatbot-gateway (which calls performance-service)
  listFiles(): Observable<PerformanceFileDto[]> {
    return this.http.get<PerformanceFileDto[]>(`${this.base}/files`);
  }

  // Get players extracted from a stored PDF
  getPlayers(fileId: number, team?: string): Observable<string[]> {
    let params = new HttpParams();
    if (team) params = params.set('team', team);
    return this.http.get<string[]>(`${this.base}/files/${fileId}/players`, { params });
  }

  // Get stats JSON for one player
  getPlayerStats(fileId: number, player: string, team?: string): Observable<any> {
    let params = new HttpParams();
    if (team) params = params.set('team', team);
    return this.http.get<any>(`${this.base}/files/${fileId}/players/${encodeURIComponent(player)}/stats`, { params });
  }
}
