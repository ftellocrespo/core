import { AuthStateEnum } from 'app/shared/model/enumerations/auth-state-enum.model';
import { CountryEnum } from 'app/shared/model/enumerations/country-enum.model';

export interface IOrganization {
  id?: number;
  identification?: string;
  name?: string;
  state?: keyof typeof AuthStateEnum;
  country?: keyof typeof CountryEnum | null;
  address?: string | null;
  phone?: string | null;
  email?: string | null;
}

export const defaultValue: Readonly<IOrganization> = {};
