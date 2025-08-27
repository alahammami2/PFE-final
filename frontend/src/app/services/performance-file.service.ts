import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../environments/environment';

export interface PerformanceFileDto {
  id: number;
  originalName: string;
  fileType: string;
  fileSize: number;
  filePath: string;
  uploadDate: string;
  performanceId?: number;
  performanceInfo?: string;
}

export interface CreatePerformanceFileRequest {
  originalName: string;
  fileType: string;
  fileSize: number;
  filePath: string; // can be empty string
  performanceId?: number;
}

@Injectable({ providedIn: 'root' })
export class PerformanceFileService {
  private baseUrl = environment.performanceServiceUrl + '/files';

  constructor(private http: HttpClient) {}

  getFiles(): Observable<PerformanceFileDto[]> {
    return this.http.get<PerformanceFileDto[]>(`${this.baseUrl}`);
  }

  createFileMetadata(payload: CreatePerformanceFileRequest): Observable<PerformanceFileDto> {
    return this.http.post<PerformanceFileDto>(`${this.baseUrl}`, payload);
  }

  // Multipart upload to store the binary in DB
  uploadBinary(file: File, performanceId?: number): Observable<PerformanceFileDto> {
    const form = new FormData();
    form.append('file', file, file.name);
    if (performanceId != null) {
      form.append('performanceId', String(performanceId));
    }
    return this.http.post<PerformanceFileDto>(`${this.baseUrl}/upload`, form);
  }

  // Download binary from DB
  downloadFile(id: number): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${id}/download`, { responseType: 'blob' as const });
  }

  deleteFile(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Count of performance files
  getCount(): Observable<number> {
    return this.http.get<any>(`${this.baseUrl}/count`).pipe(
      map((res: any) => {
        if (typeof res === 'number') return res;
        const v = res?.count ?? res?.data;
        return typeof v === 'number' ? v : Number(v) || 0;
      })
    );
  }
}
