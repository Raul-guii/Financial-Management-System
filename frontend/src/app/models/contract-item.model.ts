export interface ContractItem {
  id: number;
  name: string;
  description: string;
  quantity: number;      
  unitPrice: number;
  active: boolean;
  contractId: number;
  invoiceLineIds: number[];
}