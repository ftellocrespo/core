import dayjs from 'dayjs';
import { IUserX } from 'app/shared/model/user-x.model';

export interface IErrorLog {
  id?: number;
  className?: string | null;
  line?: number | null;
  error?: string | null;
  erroyType?: string | null;
  timestamp?: dayjs.Dayjs | null;
  user?: IUserX | null;
}

export const defaultValue: Readonly<IErrorLog> = {};
