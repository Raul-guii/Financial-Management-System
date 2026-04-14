export interface InvoiceLine {
  id: number;
  description: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
  createdAt: string;
  contractItemId: number;
  invoiceId: number;
}