export interface RefundRequestResponse {
  id: number;
  refundStatus: string;
  reason: string;
  requestedAt: string;
  approvedAt: string | null;
  paymentId: number;
}

export interface RefundRequestCreate {
  paymentId: number;
  reason: string;
}