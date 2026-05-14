export interface ReconciliationReportResponse {
  itemId: number;

  paymentId: number;

  invoiceId: number;

  gatewayTransactionId: number;

  systemAmount: number;

  gatewayAmount: number;

  status: string;
}