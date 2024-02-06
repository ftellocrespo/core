import { IOrganization } from 'app/shared/model/organization.model';
import { IUserX } from 'app/shared/model/user-x.model';
import { AuthStateEnum } from 'app/shared/model/enumerations/auth-state-enum.model';
import { RoleEnum } from 'app/shared/model/enumerations/role-enum.model';

export interface IOrganizationUser {
  id?: number;
  state?: keyof typeof AuthStateEnum | null;
  role?: keyof typeof RoleEnum | null;
  organization?: IOrganization | null;
  user?: IUserX | null;
}

export const defaultValue: Readonly<IOrganizationUser> = {};
