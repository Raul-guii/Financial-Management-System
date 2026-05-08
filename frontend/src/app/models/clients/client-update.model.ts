import { ClientType } from './client-type.enum';

export interface ClientUpdateRequest {
  name?: string;
  type?: ClientType;
  document?: string;
  email?: string;
  phone?: string;

  addressStreet?: string;
  addressNumber?: string;
  addressNeighborhood?: string;
  addressCity?: string;
  addressState?: string;
  addressPostalCode?: string;

  addressCountry?: string;
}