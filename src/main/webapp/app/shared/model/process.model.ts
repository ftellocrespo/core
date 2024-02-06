export interface IProcess {
  id?: number;
  name?: string | null;
  meta?: string | null;
}

export const defaultValue: Readonly<IProcess> = {};
