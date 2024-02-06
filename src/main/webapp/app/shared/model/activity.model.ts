import { IAction } from 'app/shared/model/action.model';
import { IProcess } from 'app/shared/model/process.model';
import { ActivityTypeEnum } from 'app/shared/model/enumerations/activity-type-enum.model';

export interface IActivity {
  id?: number;
  type?: keyof typeof ActivityTypeEnum | null;
  actions?: IAction[] | null;
  process?: IProcess | null;
}

export const defaultValue: Readonly<IActivity> = {};
