export interface MercadoPagoResponse {
  id: number;

  status: string;
  statusDetail: string;

  transactionAmount: number;

  pointOfInteraction?: PointOfInteraction;
}

export interface PointOfInteraction {
  transactionData?: TransactionData;
}

export interface TransactionData {
  qrCode?: string;
  ticketUrl?: string;
}