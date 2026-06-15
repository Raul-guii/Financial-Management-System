import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RefundRequestCreate, RefundRequestResponse } from '../../models/refund-request.model';

@Injectable({ providedIn: 'root' })
export class RefundRequestService {
  private api = `${environment.apiUrl}/refund-requests`;

  constructor(private http: HttpClient) {}

  create(data: RefundRequestCreate): Observable<RefundRequestResponse> {
    return this.http.post<RefundRequestResponse>(this.api, data);
  }

  getAll(): Observable<RefundRequestResponse[]> {
    return this.http.get<RefundRequestResponse[]>(this.api);
  }

  approve(id: number): Observable<RefundRequestResponse> {
    return this.http.patch<RefundRequestResponse>(`${this.api}/${id}/approve`, {});
  }

  reject(id: number): Observable<RefundRequestResponse> {
    return this.http.patch<RefundRequestResponse>(`${this.api}/${id}/reject`, {});
  }
}