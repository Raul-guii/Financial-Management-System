import { InvoiceStatus } from "../../core/enums/invoice.enum";

export interface Invoice {
  id: number;
  status: InvoiceStatus;
  issueDate: string;
  dueDay: string;
  amount: number;
  finalAmount: number;
  lateFreeAmount: number;
  interestAmount: number;
  createdAt: string;
  updatedAt: string;
  contractId: number;
  paymentIds: number[];
  lineIds: number[];
}