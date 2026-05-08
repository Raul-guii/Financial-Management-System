import { InvoiceStatus } from './invoice-status.enum';

export interface InvoiceUpdateRequest {
  status?: InvoiceStatus;

  issueDate?: string;
  dueDate?: string;

  amount?: number;

  contractId?: number;
}