import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuditLogResponse } from '../../../app//models/audit-log/audit-log.response.model';

@Injectable({ providedIn: 'root' })
export class AuditLogService {
  private api = `${environment.apiUrl}/audit-logs`;

  constructor(private http: HttpClient) {}

  getAll(page: number, size: number, entityType?: string, action?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (entityType) params = params.set('entityType', entityType);
    if (action) params = params.set('action', action);

    return this.http.get<any>(this.api, { params });
  }
}