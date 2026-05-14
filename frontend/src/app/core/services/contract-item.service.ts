import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ContractItemResponse } from '../../models/contract-items/contract-item-response.model';
import { ContractItemCreateRequest } from '../../models/contract-items/contract-item-create.model';
import { ContractItemUpdateRequest } from '../../models/contract-items/contract-item-update.model';

@Injectable({ providedIn: 'root' })
export class ContractItemService {
  private api = `${environment.apiUrl}/contract-items`;

  constructor(private http: HttpClient) {}

  getByContractId(contractId: number): Observable<ContractItemResponse[]> {
    return this.http.get<ContractItemResponse[]>(`${this.api}/contract/${contractId}`);
  }

  getById(id: number): Observable<ContractItemResponse> {
    return this.http.get<ContractItemResponse>(`${this.api}/${id}`);
  }

  create(data: ContractItemCreateRequest): Observable<ContractItemResponse> {
    return this.http.post<ContractItemResponse>(this.api, data);
  }

  update(id: number, data: ContractItemUpdateRequest): Observable<ContractItemResponse> {
    return this.http.put<ContractItemResponse>(`${this.api}/${id}`, data);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${id}/deactivate`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}