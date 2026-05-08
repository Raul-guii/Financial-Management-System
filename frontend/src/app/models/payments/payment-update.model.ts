import { PaymentMethod } from './payment-method.model.enum';
import { PaymentStatus } from './payment-status.model.enum';

export interface PaymentUpdateRequest {
  amount?: number;

  paymentDate?: string;

  paymentMethod?: PaymentMethod;

  paymentStatus?: PaymentStatus;

  invoiceId?: number;
}