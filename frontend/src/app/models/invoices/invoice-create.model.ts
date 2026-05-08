export interface InvoiceCreateRequest {
  issueDate: string;

  dueDate?: string;

  contractId: number;
}