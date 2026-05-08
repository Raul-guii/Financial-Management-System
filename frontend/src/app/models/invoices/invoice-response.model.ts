import { InvoiceStatus } from './invoice-status.enum';

export interface InvoiceResponse {
  id: number;

  status: InvoiceStatus;

  issueDate: string;
  dueDate?: string;

  amount: number;
  paidAmount: number;
  overpaidAmount: number;
  remainingAmount: number;

  createdAt: string;
  updatedAt: string;

  contractId: number;

  paymentIds: number[];
  lineIds: number[];
}