export interface Client {
  id: number;
  name: string;
  type: 'PERSON' | 'COMPANY';
  document: string;
  email: string;
  phone: string;
  addressStreet?: string;
  addressNumber?: string;
  addressNeighborhood?: string;
  addressCity?: string;
  addressState?: string;
  addressPostalCode?: string;
  addressCountry?: string;
  active: boolean;
  createdAt?: string;
  updatedAt?: string;
  createdBy?: number | null;
}