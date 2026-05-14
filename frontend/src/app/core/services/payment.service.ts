import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PaymentResponse } from '../../models/payments/payment-response.model';
import { PaymentCreateRequest } from '../../models/payments/payment-create.model';
import { PaymentUpdateRequest } from '../../models/payments/payment-update.model';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private api = `${environment.apiUrl}/payments`;
  private testApi = `${environment.apiUrl}/test`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(this.api);
  }

  getById(id: number): Observable<PaymentResponse> {
    return this.http.get<PaymentResponse>(`${this.api}/${id}`);
  }

  getByInvoiceId(invoiceId: number): Observable<PaymentResponse[]> {
    return this.http.get<PaymentResponse[]>(this.api).pipe(
      map(payments => payments.filter(p => p.invoiceId === invoiceId))
    );
  }

  create(data: PaymentCreateRequest): Observable<PaymentResponse> {
    return this.http.post<PaymentResponse>(this.api, data);
  }

  update(id: number, data: PaymentUpdateRequest): Observable<PaymentResponse> {
    return this.http.put<PaymentResponse>(`${this.api}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }

  simulateApproval(paymentId: number): Observable<void> {
    return this.http.post<void>(`${this.testApi}/simulate-approval/${paymentId}`, {});
  }

  simulateRefund(paymentId: number): Observable<void> {
    return this.http.post<void>(`${this.testApi}/simulate-refund/${paymentId}`, {});
  }
}