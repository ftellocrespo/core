import { ActivityTypeEnum } from 'app/shared/model/enumerations/activity-type-enum.model';

export interface IActivity {
  id?: number;
  type?: keyof typeof ActivityTypeEnum | null;
}

export const defaultValue: Readonly<IActivity> = {};
