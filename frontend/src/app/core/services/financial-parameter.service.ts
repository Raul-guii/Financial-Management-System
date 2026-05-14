import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { FinancialParameterResponse } from '../../models/financial-parameters/financial-parameter-response.model';
import { FinancialParameterCreateRequest } from '../../models/financial-parameters/financial-parameter-create.model';
import { FinancialParameterUpdateRequest } from '../../models/financial-parameters/financial-parameter-update.model';

@Injectable({ providedIn: 'root' })
export class FinancialParameterService {
  private api = `${environment.apiUrl}/financial-parameters`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<FinancialParameterResponse[]> {
    return this.http.get<FinancialParameterResponse[]>(this.api);
  }

  getById(id: number): Observable<FinancialParameterResponse> {
    return this.http.get<FinancialParameterResponse>(`${this.api}/${id}`);
  }

  create(data: FinancialParameterCreateRequest): Observable<FinancialParameterResponse> {
    return this.http.post<FinancialParameterResponse>(this.api, data);
  }

  update(id: number, data: FinancialParameterUpdateRequest): Observable<FinancialParameterResponse> {
    return this.http.put<FinancialParameterResponse>(`${this.api}/${id}`, data);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${id}/deactivate`, {});
  }
}