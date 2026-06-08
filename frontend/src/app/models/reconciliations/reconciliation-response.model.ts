import { ReconciliationItemResponse } from './reconciliation-item-response.model';

export interface ReconciliationResponse {
  id: number;
  periodStart: string;
  periodEnd: string;
  executedAt: string;
  executedById: number;
  totalIn: number;
  totalOut: number;
  netBalance: number;
  items: ReconciliationItemResponse[];
}