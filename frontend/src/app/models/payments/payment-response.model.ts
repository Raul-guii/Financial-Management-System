import { PaymentStatus } from './payment-status.model.enum';

export interface PaymentResponse {
  id: number;

  amount: number;

  paymentDate: string;

  createdAt: string;

  invoiceId: number;

  gatewayTransactionId?: number;

  refundRequestIds: number[];

  paymentStatus: PaymentStatus;

  orderId?: string;

  qrcode?: string;

  ticketUrl?: string;
}