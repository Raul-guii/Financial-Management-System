import { FinancialParameterCategory } from "./financial-parameter-category.enum.model";
import { FinancialParameterType } from "./financial-parameter-type.enum.model";

export interface FinancialParameterUpdateRequest {
  name?: string;

  value?: number;

  type?: FinancialParameterType;

  category?: FinancialParameterCategory;

  description?: string;

  active?: boolean;

  updatedById?: number;
}