import { InvoiceStatus } from "../../core/enums/invoice.enum";

export interface InvoiceUpdate {
  status?: InvoiceStatus;
  issueDate?: string;
  dueDay?: string;
  lateFreeAmount?: number;
  interestAmount?: number;
  contractId?: number;
}