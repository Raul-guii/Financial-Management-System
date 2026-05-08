import { PaymentMethod } from './payment-method.model.enum';
import { PaymentStatus } from './payment-status.model.enum';

export interface PaymentCreateRequest {
  amount: number;

  paymentDate: string;

  paymentMethod?: PaymentMethod;

  paymentStatus?: PaymentStatus;

  payerEmail?: string;

  payerFirstName: string;
  payerLastName: string;
  payerDocument: string;

  date_of_expiration?: string;

  invoiceId: number;
}