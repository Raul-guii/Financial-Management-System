export interface ContractItemCreateRequest {
  name: string;

  description?: string;

  quantity: number;

  unitPrice: number;

  active?: boolean;

  contractId: number;
}