export interface IActionGeneric {
  id?: number;
  message?: string | null;
}

export const defaultValue: Readonly<IActionGeneric> = {};
