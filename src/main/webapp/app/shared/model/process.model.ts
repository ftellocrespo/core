import { IActivity } from 'app/shared/model/activity.model';

export interface IProcess {
  id?: number;
  name?: string | null;
  meta?: string | null;
  activities?: IActivity[] | null;
}

export const defaultValue: Readonly<IProcess> = {};
