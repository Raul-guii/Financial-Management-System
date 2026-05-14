export interface ContractItemResponse {
  id: number;

  name: string;

  description?: string;

  quantity: number;

  unitPrice: number;

  active: boolean;

  contractId: number;

  invoiceLineIds: number[];
}