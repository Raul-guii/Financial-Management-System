import { InvoiceStatus } from "../../core/enums/invoice.enum";

export interface InvoiceCreate {
  status: InvoiceStatus;
  issueDate: string;
  dueDay: string;
  lateFreeAmount: number;
  interestAmount: number;
  contractId: number;
}