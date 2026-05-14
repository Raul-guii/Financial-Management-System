export interface ReconciliationItemResponse {
  id: number;

  paymentId: number;

  gatewayTransactionId: number;

  systemAmount: number;

  gatewayAmount: number;

  status: string;
}