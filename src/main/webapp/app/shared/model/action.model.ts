export interface IAction {
  id?: number;
  name?: string | null;
}

export const defaultValue: Readonly<IAction> = {};
