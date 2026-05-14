import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ClientResponse } from '../../models/clients/client-reponse.model';
import { ClientCreateRequest } from '../../models/clients/client-create.model';
import { ClientUpdateRequest } from '../../models/clients/client-update.model';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private api = `${environment.apiUrl}/clients`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<ClientResponse[]> {
    return this.http.get<ClientResponse[]>(this.api);
  }

  getById(id: number): Observable<ClientResponse> {
    return this.http.get<ClientResponse>(`${this.api}/${id}`);
  }

  create(data: ClientCreateRequest): Observable<ClientResponse> {
    return this.http.post<ClientResponse>(this.api, data);
  }

  update(id: number, data: ClientUpdateRequest): Observable<ClientResponse> {
    return this.http.put<ClientResponse>(`${this.api}/${id}`, data);
  }

  deactivate(id: number): Observable<void> {
    return this.http.patch<void>(`${this.api}/${id}/deactivate`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}