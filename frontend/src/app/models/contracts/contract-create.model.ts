import { BillingPeriod } from './billing-period.enum';
import { ContractStatus } from './contract-status.enum';

export interface ContractCreateRequest {
  status: ContractStatus;
  billingPeriod: BillingPeriod;

  startDate: string;
  endDate: string;

  clientId: number;
}