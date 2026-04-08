import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from './../../../environments/environment';
import { ContractItem } from '../../models/contract-item.model';

@Injectable({ providedIn: 'root' })
export class ContractItemService {
  private api = `${environment.apiUrl}/contract-items`;

  constructor(private http: HttpClient) {}

  getByContractId(contractId: number): Observable<ContractItem[]> {
    return this.http.get<ContractItem[]>(`${this.api}/contract/${contractId}`);
  }

  create(data: ContractItem): Observable<ContractItem> {
    return this.http.post<ContractItem>(this.api, data);
  }

  update(id: number, data: ContractItem): Observable<ContractItem> {
    return this.http.put<ContractItem>(`${this.api}/${id}`, data);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${id}/deactivate`, {});
  }
}