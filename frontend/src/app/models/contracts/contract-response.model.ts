import { BillingPeriod } from './billing-period.enum';
import { ContractStatus } from './contract-status.enum';

export interface ContractResponse {
  id: number;

  status: ContractStatus;
  billingPeriod: BillingPeriod;

  startDate: string;
  endDate: string;

  createdById: number;
  clientId: number;

  invoiceIds: number[];
  itemIds: number[];
}