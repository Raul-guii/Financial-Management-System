export interface ContractItemUpdateRequest {
  name?: string;

  description?: string;

  quantity?: number;

  unitPrice?: number;

  active?: boolean;

  contractId?: number;
}