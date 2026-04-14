import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { InvoiceCreate } from '../../models/invoices/invoice-create.model';
import { Invoice } from '../../models/invoices/invoice.model';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  private API = `${environment.apiUrl}/invoices`;

  constructor(private http: HttpClient) {}

  create(data: InvoiceCreate): Observable<Invoice> {
    return this.http.post<Invoice>(this.API, data);
  }

  findById(id: number): Observable<Invoice> {
    return this.http.get<Invoice>(`${this.API}/${id}`);
  }

  findAll(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(this.API);
  }

  pay(id: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.API}/${id}/pay`, {});
  }

  refund(id: number): Observable<Invoice> {
    return this.http.post<Invoice>(`${this.API}/${id}/refund`, {});
  }
}