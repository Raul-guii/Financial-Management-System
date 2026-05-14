import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InvoiceResponse } from '../../models/invoices/invoice-response.model';
import { InvoiceUpdateRequest } from '../../models/invoices/invoice-update.model';
import { InvoiceCreateRequest } from '../../models/invoices/invoice-create.model';

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private api = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<InvoiceResponse[]> {
    return this.http.get<InvoiceResponse[]>(this.api);
  }

  getById(id: number): Observable<InvoiceResponse> {
    return this.http.get<InvoiceResponse>(`${this.api}/${id}`);
  }

  create(data: InvoiceCreateRequest): Observable<InvoiceResponse> {
    return this.http.post<InvoiceResponse>(this.api, data);
  }

  update(id: number, data: InvoiceUpdateRequest): Observable<InvoiceResponse> {
    return this.http.put<InvoiceResponse>(`${this.api}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}