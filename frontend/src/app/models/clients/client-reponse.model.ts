import { ClientType } from './client-type.enum';

export interface ClientResponse {
  id: number;

  name: string;
  type: ClientType;

  defaulter: boolean;

  document: string;
  email: string;
  phone: string;

  addressStreet?: string;
  addressNumber?: string;
  addressNeighborhood?: string;
  addressCity?: string;
  addressState?: string;
  addressPostalCode?: string;

  addressCountry: string;

  active: boolean;

  createdAt: string;
  updatedAt: string;

  createdBy: number;
}