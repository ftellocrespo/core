import { AuthStateEnum } from 'app/shared/model/enumerations/auth-state-enum.model';
import { RoleEnum } from 'app/shared/model/enumerations/role-enum.model';

export interface IOrganizationUser {
  id?: number;
  state?: keyof typeof AuthStateEnum | null;
  role?: keyof typeof RoleEnum | null;
}

export const defaultValue: Readonly<IOrganizationUser> = {};
