import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InvoiceResponse } from '../../models/invoices/invoice-response.model';
import { InvoiceUpdateRequest } from '../../models/invoices/invoice-update.model';
import { InvoiceCreateRequest } from '../../models/invoices/invoice-create.model';
import { Page } from '../../models/pages/page.model';

@Injectable({ providedIn: 'root' })
export class InvoiceService {
  private api = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  getAll(page: number = 0, size: number = 20, search: string = ''): Observable<Page<InvoiceResponse>> {
    const params = search
      ? `?page=${page}&size=${size}&search=${encodeURIComponent(search)}`
      : `?page=${page}&size=${size}`;
    return this.http.get<Page<InvoiceResponse>>(`${this.api}${params}`);
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