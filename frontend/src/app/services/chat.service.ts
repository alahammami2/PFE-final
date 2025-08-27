// src/app/services/chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface ChatResponse {
  answer: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
    private base = environment.performanceServiceUrl;
    private apiUrl = `${this.base}/chat`;
  constructor(private http: HttpClient) { }

  sendMessage(query: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(this.apiUrl, { query });
  }

  // Ask a question about a specific PDF stored in the DB
  askPdf(fileId: number, question: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(`${this.apiUrl}/pdf`, { fileId, question });
  }
}