export interface InvoiceLineCreateRequest {
  description: string;

  quantity: number;

  unitPrice: number;

  contractItemId?: number;

  invoiceId: number;
}