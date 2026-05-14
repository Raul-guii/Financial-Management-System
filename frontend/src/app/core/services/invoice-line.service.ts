import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InvoiceLineResponse } from '../../models/invoice-line/invoice-line-response.model';

@Injectable({ providedIn: 'root' })
export class InvoiceLineService {
  private api = `${environment.apiUrl}/invoice-lines`;

  constructor(private http: HttpClient) {}

  getById(id: number): Observable<InvoiceLineResponse> {
    return this.http.get<InvoiceLineResponse>(`${this.api}/${id}`);
  }

  getByInvoiceId(invoiceId: number): Observable<InvoiceLineResponse[]> {
    return this.http.get<InvoiceLineResponse[]>(`${this.api}/invoice/${invoiceId}`);
  }
}