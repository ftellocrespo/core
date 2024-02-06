import dayjs from 'dayjs';
import { AuthStateEnum } from 'app/shared/model/enumerations/auth-state-enum.model';

export interface IUserX {
  id?: number;
  email?: string;
  password?: string;
  facebookId?: string | null;
  googleId?: string | null;
  firstName?: string | null;
  lastName?: string | null;
  phone?: string | null;
  birth?: dayjs.Dayjs | null;
  gender?: string | null;
  nationality?: string | null;
  address?: string | null;
  state?: keyof typeof AuthStateEnum;
}

export const defaultValue: Readonly<IUserX> = {};
