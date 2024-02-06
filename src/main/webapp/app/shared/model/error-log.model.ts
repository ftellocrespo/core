import dayjs from 'dayjs';

export interface IErrorLog {
  id?: number;
  className?: string | null;
  line?: number | null;
  error?: string | null;
  erroyType?: string | null;
  timestamp?: dayjs.Dayjs | null;
}

export const defaultValue: Readonly<IErrorLog> = {};
