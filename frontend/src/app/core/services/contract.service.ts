import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ContractResponse } from '../../models/contracts/contract-response.model';
import { ContractUpdateRequest } from '../../models/contracts/contract-update.model';
import { ContractCreateRequest } from '../../models/contracts/contract-create.model';
import { Page } from '../../models/pages/page.model';

@Injectable({ providedIn: 'root' })
export class ContractService {
  private api = `${environment.apiUrl}/contracts`;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 20, search: string = ''): Observable<Page<ContractResponse>> {
    const params = search
      ? `?page=${page}&size=${size}&search=${encodeURIComponent(search)}`
      : `?page=${page}&size=${size}`;
    return this.http.get<Page<ContractResponse>>(`${this.api}${params}`);
  }

  getById(id: number): Observable<ContractResponse> {
    return this.http.get<ContractResponse>(`${this.api}/${id}`);
  }

  getByClientId(clientId: number): Observable<ContractResponse[]> {
    return this.http.get<ContractResponse[]>(`${this.api}/client/${clientId}`);
  }

  create(data: ContractCreateRequest): Observable<ContractResponse> {
    return this.http.post<ContractResponse>(this.api, data);
  }

  update(id: number, data: ContractUpdateRequest): Observable<ContractResponse> {
    return this.http.put<ContractResponse>(`${this.api}/${id}`, data);
  }

  cancel(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${id}/cancel`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}