import { IActivity } from 'app/shared/model/activity.model';

export interface IAction {
  id?: number;
  name?: string | null;
  activity?: IActivity | null;
}

export const defaultValue: Readonly<IAction> = {};
