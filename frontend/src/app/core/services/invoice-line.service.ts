import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InvoiceLine } from '../../models/invoices/invoice-line.model';

@Injectable({
  providedIn: 'root'
})
export class InvoiceLineService {

  private API = `${environment.apiUrl}/invoice-lines`;

  constructor(private http: HttpClient) {}

  findById(id: number): Observable<InvoiceLine> {
    return this.http.get<InvoiceLine>(`${this.API}/${id}`);
  }

  findByInvoice(invoiceId: number): Observable<InvoiceLine[]> {
    return this.http.get<InvoiceLine[]>(`${this.API}/invoice/${invoiceId}`);
  }
}