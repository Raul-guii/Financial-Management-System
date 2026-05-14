import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ReconciliationResponse } from '../../models/reconciliations/reconciliation-response.model';
import { ReconciliationCreateRequest } from '../../models/reconciliations/reconciliation-create.model';

@Injectable({ providedIn: 'root' })
export class ReconciliationService {
  private api = `${environment.apiUrl}/reconciliations`;
  private reportApi = `${environment.apiUrl}/reports`;

  constructor(private http: HttpClient) {}

  execute(data: ReconciliationCreateRequest): Observable<ReconciliationResponse> {
    return this.http.post<ReconciliationResponse>(this.api, data);
  }

  exportCsv(): Observable<Blob> {
    return this.http.get(`${this.reportApi}/reconciliation`, {
      responseType: 'blob'
    });
  }
}